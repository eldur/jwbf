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

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main interaction class.
 *
 * @author Thomas Stock
 */
public class HttpActionClient {

  private static final Logger log = LoggerFactory.getLogger(HttpActionClient.class);
  private static final String USER_AGENT = "JWBF " + JWBF.getVersion(HttpActionClient.class);

  private final HttpClient client;

  private final String path;

  private final HttpHost host;

  private int prevHash;

  private final Optional<RateLimiter> rateLimiter;

  private final URL url;

  public HttpActionClient(final URL url) {
    this(HttpClientBuilder.create(), url);
  }

  /**
   * @param url like "http://host/of/wiki/"
   */
  public HttpActionClient(final HttpClientBuilder clientBuilder, final URL url) {

    this.url = url;
    path = pathOf(url);
    host = newHost(url);
    rateLimiter = Optional.absent();
    this.client = clientBuilder.build();
  }

  public HttpActionClient(Builder builder) {
    this.url = Preconditions.checkNotNull(builder.url, "no url is defined");
    host = newHost(builder.url);
    path = pathOf(builder.url);
    if (builder.requestsPerSecond > 0) {
      rateLimiter = Optional.of(RateLimiter.create(builder.requestsPerSecond));
    } else {
      rateLimiter = Optional.absent();
    }

    this.client = builder.client;
  }

  private HttpHost newHost(final URL url) {
    return new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
  }

  private String pathOf(final URL url) {
    String urlPath = url.getPath();
    if (urlPath.length() > 1) {
      return urlPath.substring(0, urlPath.lastIndexOf("/"));
    } else {
      return "";
    }
  }

  /**
   * @return message, never null
   */
  @Nonnull
  public synchronized String performAction(ContentProcessable contentProcessable) {
    String out = "";
    while (contentProcessable.hasMoreMessages()) {
      HttpAction httpAction = contentProcessable.getNextMessage();
      ReturningTextProcessor answerParser = contentProcessable;
      out = processAction(httpAction, answerParser);

    }
    return out;
  }

  protected String processAction(HttpAction httpAction, ReturningTextProcessor answerParser) {
    final String requestString = makeRequestString(httpAction);
    log.debug(requestString);
    URI uri = JWBF.toUri(host.toURI() + requestString);
    if (httpAction instanceof Get) {
      HttpRequestBase httpRequest = new HttpGet(uri);

      return get(httpRequest, answerParser, httpAction);
    } else if (httpAction instanceof Post) {
      HttpRequestBase httpRequest = new HttpPost(uri);

      return post(httpRequest, answerParser, httpAction);
    }
    throw new IllegalArgumentException("httpAction should be GET or POST");
  }

  private String makeRequestString(HttpAction httpAction) {
    final String requestString;
    if (path.length() > 1) {
      requestString = path + httpAction.getRequest();
    } else {
      requestString = httpAction.getRequest();
    }
    return requestString;
  }

  private String post(HttpRequestBase requestBase //
      , ReturningTextProcessor contentProcessable, HttpAction ha) {
    Post p = (Post) ha;
    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
    for (String key : p.getParams().keySet()) {
      Object content = p.getParams().get(key);
      if (content != null) {
        if (content instanceof String) {
          Charset charset = Charset.forName(p.getCharset());
          String text = (String) content;
          entityBuilder.addTextBody(key, text, ContentType.create("xml/text", charset));
        } else if (content instanceof File) {
          File file = (File) content;
          entityBuilder.addBinaryBody(key, file);
        }
      }
    }
    ((HttpPost) requestBase).setEntity(entityBuilder.build());

    return executeAndProcess(requestBase, contentProcessable, ha);

  }

  protected void consume(HttpResponse res) {
    try {
      res.getEntity().getContent().close();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Nonnull
  private String get(HttpRequestBase requestBase, ReturningTextProcessor cp, HttpAction ha) {
    return executeAndProcess(requestBase, cp, ha);
  }

  private String executeAndProcess(HttpRequestBase requestBase, ReturningTextProcessor cp,
      HttpAction ha) {

    log.debug("message {} is: " //
        + "\n\t hostPath : {} " //
        + "\n\t queryPath: {}", debug(requestBase, ha, cp));
    HttpResponse res = execute(requestBase);

    final String out = writeToString(ha, res);
    try {
      if (cp != null) {
        return cp.processReturningText(out, ha);
      } else {
        return out;
      }
    } finally {
      consume(res);
    }
  }

  @Nonnull
  private String writeToString(HttpAction ha, HttpResponse res) {
    StringBuilder sb = new StringBuilder();
    Charset charSet = Charset.forName(ha.getCharset());
    try ( //
        InputStreamReader inputStreamReader = new InputStreamReader(res.getEntity().getContent(),
            charSet); //
        BufferedReader br = new BufferedReader(inputStreamReader); //
    ) {

      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return sb.toString();
  }

  private HttpResponse execute(HttpRequestBase requestBase) {
    HttpResponse res = null;
    try {
      if (rateLimiter.isPresent()) {
        rateLimiter.get().acquire();
      }
      res = client.execute(requestBase);
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
    HttpGet authgets = new HttpGet(get.getRequest());
    return get(authgets, null, get).getBytes();
  }

  @VisibleForTesting
  Object[] debug(HttpUriRequest request, HttpAction ha, ReturningTextProcessor cp) {
    if (cp != null) {
      final String continueing = debugContinueingMsg(cp);
      final String path = debugRequestPathOf(request);
      final String type = debugTypeOf(ha, cp, continueing);
      return new String[] { type, path, ha.getRequest() };
    }
    return new String[0];
  }

  private String debugRequestPathOf(HttpUriRequest request) {
    String requestString = request.getURI().toString();
    int lastSlash = requestString.lastIndexOf("/");
    requestString = requestString.substring(0, lastSlash);
    return requestString;
  }

  private String debugContinueingMsg(ReturningTextProcessor cp) {
    final String continueing;
    // FIXME internal state mutating on debug WTF
    if (prevHash == cp.hashCode()) {
      continueing = " [continuing req]";
    } else {
      continueing = "";
    }
    prevHash = cp.hashCode();
    return continueing;
  }

  private String debugTypeOf(HttpAction ha, ReturningTextProcessor cp, final String continueing) {
    String className = cp.getClass().getName();
    final String suffix = className + ")" + continueing;
    if (ha instanceof Post) {
      return "(POST " + suffix;
    } else if (ha instanceof Get) {
      return "(GET " + suffix;
    } else {
      throw new IllegalStateException("unknown type: " + ha.getClass().getCanonicalName());
    }
  }

  /**
   * @return like http://localhost
   */
  String getHostUrl() {
    return host.toURI();
  }

  /**
   * @return like http://localhost/a/b?c=d
   */
  public String getUrl() {
    return url.toExternalForm();
  }

  public static class Builder {

    private double requestsPerSecond = -1;
    private HttpClient client;
    private URL url;
    private String userAgent;

    public Builder withUserAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    public HttpActionClient build() {
      HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
      if (client == null) {
        if (Strings.isNullOrEmpty(userAgent)) {
          userAgent = USER_AGENT;
        }
        httpClientBuilder.setUserAgent(userAgent);
        withClient(httpClientBuilder.build());

      } else {
        if (userAgent != null) {
          String msg = "useragent must be setted in your client";
          throw new IllegalArgumentException(msg);
        }
      }
      return new HttpActionClient(this);
    }

    public Builder withClient(HttpClient client) {
      this.client = client;
      return this;
    }

    public Builder withUrl(URL url) {
      this.url = url;
      return this;
    }

    public Builder withUrl(String url) {
      return withUrl(JWBF.newURL(url));
    }

    public Builder withRequestsPerUnit(double requestsPer, TimeUnit unit) {
      long seconds = TimeUnit.SECONDS.convert(1, unit);
      this.requestsPerSecond = requestsPer / seconds;
      return this;
    }
  }

  public static HttpActionClient of(String url) {
    return builder().withUrl(url).build();
  }

  public static HttpActionClient of(URL url) {
    return builder().withUrl(url).build();
  }

  public static Builder builder() {
    return new Builder();
  }

}
