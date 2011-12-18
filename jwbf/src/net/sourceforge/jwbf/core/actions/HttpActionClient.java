/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */

package net.sourceforge.jwbf.core.actions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.CookieException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;


/**
 * The main interaction class.
 *
 * @author Thomas Stock
 *
 */
@Slf4j
public class HttpActionClient {

  private HttpClient client;

  private String path = "";

  private HttpHost host;

  private int prevHash;




  public HttpActionClient(final URL url) {
    this(new DefaultHttpClient(), url);
  }
  /**
   *
   * @param client
   *            a
   * @param url
   *            like "http://host/of/wiki/"
   */
  public HttpActionClient(final HttpClient client, final URL url) {

    /*
     * see for docu
     * http://jakarta.apache.org/commons/httpclient/preference-api.html
     */


    if (url.getPath().length() > 1) {
      path = url.getPath().substring(0, url.getPath().lastIndexOf("/"));
    }
    client.getParams().setParameter("http.useragent",
        "JWBF " + JWBF.getVersion(getClass())); // some wikis (e.g. Wikipedia) need this line
    client.getParams().setParameter("http.protocol.expect-continue", Boolean.FALSE); // is good for wikipedia server
    host = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

    this.client = client;
  }



  /**
   *
   * @param contentProcessable
   *            a
   * @return message, never null
   * @throws ActionException
   *             on problems with http, cookies and io
   * @throws ProcessException on inner problems
   */
  public synchronized String performAction(ContentProcessable contentProcessable)
      throws ActionException, ProcessException {

    String out = "";
    while (contentProcessable.hasMoreMessages()) {

      HttpRequestBase httpRequest = null;
      try {

        HttpAction httpAction = contentProcessable.getNextMessage();

        final String request;
        if (path.length() > 1) {
          request = path + httpAction.getRequest();
        } else {
          request = httpAction.getRequest();
        }
        log.debug(request);
        if (httpAction instanceof Get) {
          httpRequest = new HttpGet(request);


          modifyRequestParams(httpRequest, httpAction);

          // do get
          out = get(httpRequest, contentProcessable, httpAction);
        } else if (httpAction instanceof Post) {

          httpRequest = new HttpPost(request);
          modifyRequestParams(httpRequest, httpAction);

          // do post
          out = post(httpRequest, contentProcessable, httpAction);
        }


      } catch (IOException e1) {
        throw new ActionException(e1);
      } catch (IllegalArgumentException e2) {
        e2.printStackTrace();
        throw new ActionException(e2);
      }

    }
    return out;

  }

  private void modifyRequestParams(HttpRequestBase request, HttpAction httpAction) {
    HttpParams params = request.getParams();
    params.setParameter(ClientPNames.DEFAULT_HOST, host);
    params.setParameter("http.protocol.content-charset",
        httpAction.getCharset());
  }

  private String post(HttpRequestBase requestBase, ContentProcessable contentProcessable, HttpAction ha)
      throws IOException, CookieException, ProcessException {
    Post p = (Post) ha;
    MultipartEntity entity = new MultipartEntity();
    for (String key : p.getParams().keySet()) {
      Object content = p.getParams().get(key);
      if (content != null) {
        if (content instanceof String)
          entity.addPart(key, new StringBody((String) content, Charset.forName(p.getCharset())));
        else if (content instanceof File)
          entity.addPart(key, new FileBody((File) content));
      }
    }
    ((HttpPost) requestBase).setEntity(entity);
    debug(requestBase, ha, contentProcessable);
    HttpResponse res = execute(requestBase);


    ByteArrayOutputStream byte1 = new ByteArrayOutputStream();

    res.getEntity().writeTo(byte1);
    String out = new String(byte1.toByteArray());
    out = contentProcessable.processReturningText(out, ha);

    if (contentProcessable instanceof CookieValidateable && client instanceof DefaultHttpClient)
      ((CookieValidateable) contentProcessable).validateReturningCookies(cookieTransform(
          ((DefaultHttpClient)client).getCookieStore().getCookies()), ha);
    res.getEntity().consumeContent();

    return out;

  }
  /**
   * Process a GET Message.
   *
   * @param requestBase
   *            a
   * @param cp
   *            a
   * @return a returning message, not null
   * @throws IOException on problems
   * @throws CookieException on problems
   * @throws ProcessException on problems
   */
  private String get(HttpRequestBase requestBase, ContentProcessable cp, HttpAction ha)
      throws IOException, CookieException, ProcessException {
    showCookies();
    debug(requestBase, ha, cp);
    String out = "";

    HttpResponse res = execute(requestBase);

    StringBuffer sb = new StringBuffer();
    BufferedReader br = null;
    try {
      Charset charSet = Charset.forName(ha.getCharset());
      //      Header header = res.getEntity().getContentType();
      //      if (header != null) {
      //        System.out.println(res.getLastHeader("Content-Encoding"));
      //
      //      }

      br = new BufferedReader(new InputStreamReader(res
          .getEntity().getContent(), charSet));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } finally {
      if (br != null)
        br.close();
    }

    out = sb.toString();
    if (cp != null) {
      if (cp instanceof CookieValidateable
          && client instanceof DefaultHttpClient)
        ((CookieValidateable) cp).validateReturningCookies(
            cookieTransform(((DefaultHttpClient) client)
                .getCookieStore().getCookies()), ha);
      out = cp.processReturningText(out, ha);
    }
    res.getEntity().consumeContent();
    return out;
  }

  private HttpResponse execute(HttpRequestBase requestBase) throws IOException,
  ClientProtocolException, ProcessException {
    HttpResponse res = client.execute(requestBase);
    StatusLine statusLine = res.getStatusLine();
    int code = statusLine.getStatusCode();
    if (code >= HttpStatus.SC_BAD_REQUEST) {
      throw new ProcessException("invalid status: " + statusLine + "; for " + requestBase.getURI());
    }
    return res;
  }

  /**
   * Process a GET Message.
   * @param get
   *            a
   * @return a returning message, not null
   * @throws IOException on problems
   * @throws CookieException on problems
   * @throws ProcessException on problems
   */
  public byte[] get(Get get)
      throws IOException, CookieException, ProcessException {
    showCookies();
    HttpGet authgets = new HttpGet(get.getRequest());
    return get(authgets, null, get).getBytes();
  }

  private Map<String, String> cookieTransform(List<Cookie> ca) {
    Map<String, String> m = new HashMap<String, String>();
    for (Cookie cookie : ca) {
      m.put(cookie.getName(), cookie.getValue());
    }
    return m;
  }
  /**
   * send the cookies to the logger.
   *
   * @param client
   *            a
   *            @deprecated is a bit too chatty
   */
  @Deprecated
  private void showCookies() {
    if (client instanceof DefaultHttpClient  && log.isDebugEnabled()) {
      List<Cookie> cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
      if (cookies.size() > 0) {
        StringBuffer cStr = new StringBuffer();
        for (Cookie cookie : cookies) {
          cStr.append(cookie.toString() + ", ");
        }
        log.debug("cookie: {" + cStr + "}");
      }
    }
  }


  private void debug(HttpUriRequest e, HttpAction ha, ContentProcessable cp) {
    if (log.isDebugEnabled() && cp != null) {

      String continueing = "";
      if (prevHash == cp.hashCode()) {
        continueing = " [continuing req]";
      } else {
        continueing = "";
      }
      prevHash = cp.hashCode();
      String epath = e.getURI().toString();
      int sl = epath.lastIndexOf("/");
      epath = epath.substring(0, sl);
      String type = "";
      if (ha instanceof Post) {
        type = "(POST ";
      } else if (ha instanceof Get) {
        type = "(GET ";
      }
      type += cp.getClass().getSimpleName() + ")" + continueing;
      log.debug("message " + type + " is: \n\t own: "
          +  getHostUrl()
          + epath + "\n\t act: " + ha.getRequest());
    }
  }
  /**
   *
   * @return the
   */
  public String getHostUrl() {
    return host.toURI();
  }
}

