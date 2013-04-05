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
 * Philipp Kohl
 */
package net.sourceforge.jwbf.core.bots;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.GetPage;
import net.sourceforge.jwbf.core.actions.HttpActionClient;

/**
 * 
 * @author Thomas Stock
 * 
 */

public class HttpBot {

  private HttpActionClient actionClient;

  private String url;

  /**
   * do nothing, but keep in mind, that you have to setup the connection
   */
  public HttpBot() {

  }

  /**
   * @param url
   *          of the host
   */
  public HttpBot(final String url) {
    this.url = url;
    setClient(newURL(url));
  }

  public static URL newURL(final String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public HttpBot(HttpActionClient actionClient) {
    this.actionClient = actionClient;
  }

  /**
   * 
   * @param url
   *          of the host
   */
  public HttpBot(final URL url) {
    setClient(url);
  }

  /**
   * Returns a {@link HttpBot} which supports only its basic methods. Use {@link #getPage(String)}
   * for an basic read of content.
   * 
   * @deprecated do not use this
   * @return a
   */
  @Deprecated
  public static HttpBot getInstance() {

    try {
      return new HttpBot(new URL("http://localhost/"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * 
   * @param client
   *          if you whant to add some specials
   * 
   */
  public final void setClient(final HttpActionClient client) {
    client.getClass();
    actionClient = client;
  }

  /**
   * 
   * @param hostUrl
   *          base url of a wiki site to connect with; example: http://www.yourOwnWiki.org/wiki/
   */
  public final void setClient(final String hostUrl) {
    setClient(newURL(hostUrl));
  }

  /**
   * 
   * @param hostUrl
   *          like http://www.yourOwnWiki.org/wiki/
   */
  public final void setClient(final URL hostUrl) {
    setClient(new HttpActionClient(hostUrl));
  }

  /**
   * 
   * @return a
   */
  public final HttpActionClient getClient() {
    return actionClient;
  }

  public final String getHostUrl() {
    return actionClient.getHostUrl();
  }

  /**
   * 
   * @return http raw content
   */
  public synchronized String performAction(final ContentProcessable a) {
    return actionClient.performAction(a);
  }

  /**
   * Simple method to get plain HTML or XML data e.g. from custom specialpages or xml newsfeeds.
   * 
   * @param u
   *          url like index.php?title=Main_Page
   * @return HTML content
   */
  public final String getPage(String u) {

    URL url = newURL(u);
    setClient(url.getProtocol() + "://" + url.getHost());
    GetPage gp = new GetPage(u);
    performAction(gp);
    return gp.getText();
  }

  /**
   * Simple method to get plain HTML or XML data e.g. from custom specialpages or xml newsfeeds.
   * 
   * @param u
   *          url like index.php?title=Main_Page
   * @return HTML content
   */
  public final byte[] getBytes(String u) {
    return actionClient.get(new Get(u));
  }

  /**
   * 
   * TODO check usage of hosturl
   * 
   * @deprecated
   */
  @Deprecated
  public String getUrl() {
    return url;
  }

}
