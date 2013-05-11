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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.actions.util.HttpAction;

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
   * @param url
   *          like "http://host/of/wiki/"
   */
  public HttpActionClient(final HttpClient client, final URL url) {

    /*
     * see for docu http://jakarta.apache.org/commons/httpclient/preference-api.html
     */

    if (url.getPath().length() > 1) {
      path = url.getPath().substring(0, url.getPath().lastIndexOf("/"));
    }
    client.getParams().setParameter("http.useragent" //
        , "JWBF " + JWBF.getVersion(getClass()));
    client.getParams() //
        .setParameter("http.protocol.expect-continue", Boolean.FALSE);
    // is good for wikipedia server
    host = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());

    this.client = client;
  }

  /**
   * 
   * @return message, never null
   */
  @Nonnull
  public synchronized String performAction(ContentProcessable contentProcessable) {
    String out = "";
    while (contentProcessable.hasMoreMessages()) {
      HttpAction httpAction = contentProcessable.getNextMessage();
      ReturningText answerParser = contentProcessable;
      out = processAction(httpAction, answerParser);

    }
    return out;

  }

  protected String processAction(HttpAction httpAction, ReturningText answerParser) {
    final String request;
    if (path.length() > 1) {
      request = path + httpAction.getRequest();
    } else {
      request = httpAction.getRequest();
    }
    HttpRequestBase httpRequest = new HttpGet(request);
    log.debug(request);
    if (httpAction instanceof Get) {
      modifyRequestParams(httpRequest, httpAction);

      // do get
      return get(httpRequest, answerParser, httpAction);
    } else if (httpAction instanceof Post) {

      httpRequest = new HttpPost(request);
      modifyRequestParams(httpRequest, httpAction);

      // do post
      return post(httpRequest, answerParser, httpAction);
    }
    throw new IllegalArgumentException("httpAction should be GET or POST");
  }

  private void modifyRequestParams(HttpRequestBase request, HttpAction httpAction) {
    HttpParams params = request.getParams();
    params.setParameter(ClientPNames.DEFAULT_HOST, host);
    params.setParameter("http.protocol.content-charset", httpAction.getCharset());
  }

  private String post(HttpRequestBase requestBase //
      , ReturningText contentProcessable, HttpAction ha) {
    Post p = (Post) ha;
    MultipartEntity entity = new MultipartEntity();
    for (String key : p.getParams().keySet()) {
      Object content = p.getParams().get(key);
      if (content != null) {
        if (content instanceof String) {
          Charset charset = Charset.forName(p.getCharset());
          entity.addPart(key, newStringBody((String) content, charset));
        } else if (content instanceof File) {
          entity.addPart(key, new FileBody((File) content));
        }
      }
    }
    ((HttpPost) requestBase).setEntity(entity);
    debug(requestBase, ha, contentProcessable);
    HttpResponse res = execute(requestBase);

    String out = writeToString(ha, res);

    out = contentProcessable.processReturningText(out, ha);

    validateCookies(contentProcessable, ha);
    consume(res);

    return out;

  }

  private void validateCookies(ReturningText contentProcessable, HttpAction ha) {
    if (contentProcessable instanceof CookieValidateable //
        && client instanceof DefaultHttpClient) {
      CookieValidateable cookieValidateable = (CookieValidateable) contentProcessable;
      DefaultHttpClient defaultHttpClient = (DefaultHttpClient) client;
      List<Cookie> cookies = defaultHttpClient.getCookieStore().getCookies();
      cookieValidateable.validateReturningCookies(cookieTransform(cookies), ha);
    }
  }

  private StringBody newStringBody(String content, Charset charset) {
    try {
      return new StringBody(content, charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  protected void consume(HttpResponse res) {
    try {
      res.getEntity().getContent().close();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Nonnull
  private String get(HttpRequestBase requestBase, ReturningText cp, HttpAction ha) {
    traceCookies();
    debug(requestBase, ha, cp);
    String out = "";

    HttpResponse res = execute(requestBase);

    out = writeToString(ha, res);

    if (cp != null) {
      validateCookies(cp, ha);
      out = cp.processReturningText(out, ha);
    }
    consume(res);
    return out;
  }

  private String writeToString(HttpAction ha, HttpResponse res) {
    StringBuffer sb = new StringBuffer();
    BufferedReader br = null;
    try {
      Charset charSet = Charset.forName(ha.getCharset());

      br = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), charSet));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }
    return sb.toString();
  }

  private HttpResponse execute(HttpRequestBase requestBase) {
    HttpResponse res = null;
    try {
      res = client.execute(requestBase);
    } catch (ClientProtocolException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    StatusLine statusLine = res.getStatusLine();
    int code = statusLine.getStatusCode();
    if (code >= HttpStatus.SC_BAD_REQUEST) {
      consume(res);
      throw new IllegalStateException("invalid status: " + statusLine + "; for "
          + requestBase.getURI());
    }
    return res;
  }

  @Nonnull
  public byte[] get(Get get) {
    traceCookies();
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

  private void traceCookies() {
    if (log.isTraceEnabled() && client instanceof DefaultHttpClient) {
      List<Cookie> cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
      if (cookies.size() > 0) {
        StringBuffer cStr = new StringBuffer();
        for (Cookie cookie : cookies) {
          cStr.append(cookie.toString() + ", ");
        }
        log.trace("cookie: {" + cStr + "}");
      }
    }
  }

  private void debug(HttpUriRequest e, HttpAction ha, ReturningText cp) {
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
      log.debug("message " + type + " is: " //
          + "\n\t hostPath : " + getHostUrl() + epath //
          + "\n\t queryPath: " + ha.getRequest());
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
