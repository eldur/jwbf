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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.GetPage;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;

/**
 * 
 * @author Thomas Stock
 * 
 */

public class HttpBot {

  private HttpActionClient cc;

  private String url;

  /**
   * do nothing, but keep in mind, that you have to setup the connection
   */
  public HttpBot() {

  }

  /**
   * Design for extension.
   * 
   * @param url
   *          of the host
   */
  public HttpBot(final String url) {
    this.url = url;
    try {
      setConnection(new URL(url));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Design for extension.
   * 
   * @param cc
   *          a
   */
  public HttpBot(HttpActionClient cc) {
    this.cc = cc;
  }

  /**
   * Design for extension.
   * 
   * @param url
   *          of the host
   */
  public HttpBot(final URL url) {
    setConnection(url);
  }

  /**
   * Returns a {@link HttpBot} which supports only its basic methods. Use
   * {@link #getPage(String)} for an basic read of content.
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
  public final void setConnection(final HttpActionClient client) {
    client.getClass();
    cc = client;
  }

  public final String getHostUrl() {
    return cc.getHostUrl();
  }

  /**
   * 
   * @param a
   *          a
   * @return text
   */
  public synchronized String performAction(final ContentProcessable a) {
    return cc.performAction(a);
  }

  /**
   * 
   * @param hostUrl
   *          base url of a wiki site to connect with; example:
   *          http://www.yourOwnWiki.org/wiki/
   */
  public final void setConnection(final String hostUrl) {
    try {
      setConnection(new URL(hostUrl));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Simple method to get plain HTML or XML data e.g. from custom specialpages
   * or xml newsfeeds.
   * 
   * @param u
   *          url like index.php?title=Main_Page
   * @return HTML content
   */
  public final String getPage(String u) {

    try {
      URL url = new URL(u);

      setConnection(url.getProtocol() + "://" + url.getHost());
    } catch (MalformedURLException e) {
      throw new ActionException(e);
    }

    GetPage gp = new GetPage(u);

    try {
      performAction(gp);
    } catch (ProcessException e) {
      throw new ActionException(e);
    }

    return gp.getText();
  }

  /**
   * Simple method to get plain HTML or XML data e.g. from custom specialpages
   * or xml newsfeeds.
   * 
   * @param u
   *          url like index.php?title=Main_Page
   * @return HTML content
   */
  public final byte[] getBytes(String u) {

    try {
      return cc.get(new Get(u));
    } catch (ProcessException e) {
      throw new ActionException(e);
    } catch (IOException e) {
      throw new ActionException(e);
    }
  }

  /**
   * 
   * @return a
   */
  public final HttpActionClient getClient() {
    return cc;
  }

  /**
   * 
   * @param hostUrl
   *          like http://www.yourOwnWiki.org/wiki/
   */
  public final void setConnection(final URL hostUrl) {
    setConnection(new HttpActionClient(hostUrl));

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
