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

import java.net.URL;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.GetPage;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.HttpActionClient.Builder;
import net.sourceforge.jwbf.core.internal.Checked;

/** @author Thomas Stock */
public class HttpBot {

  private final HttpActionClient actionClient;

  public HttpBot(HttpActionClient actionClient) {
    this.actionClient = Checked.nonNull(actionClient, "actionClient");
  }

  public HttpBot(final String url) {
    this(clientBuilder().withUrl(url).build());
  }

  public HttpBot(final URL url) {
    this(clientBuilder().withUrl(url).build());
  }

  private static Builder clientBuilder() {
    return HttpActionClient.builder();
  }

  /** @return http raw content */
  public synchronized String performAction(final ContentProcessable a) {
    return actionClient.performAction(a);
  }

  public static String getPage(final HttpActionClient client) {
    GetPage gp = new GetPage(client.getUrl());
    new HttpBot(client).performAction(gp);
    return gp.getText();
  }

  /**
   * Simple method to get plain HTML or XML data e.g. from custom specialpages or xml newsfeeds.
   *
   * @param url like index.php?title=Main_Page
   * @return HTML content
   */
  public static String getPage(String url) {
    return getPage(HttpActionClient.of(url));
  }
}
