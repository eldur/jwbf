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

import net.sourceforge.jwbf.core.actions.util.HttpAction;

/**
 * Simple method to get plain HTML or XML data e.g. from custom specialpages or xml newsfeeds or something else.
 *
 * @author Thomas Stock
 */
public class GetPage implements ContentProcessable {

  private final HttpAction msg;
  private boolean hasMore = true;
  private String text = "";

  /**
   * @param u       like "/index.php?title=Special:Recentchanges&feed=rss"
   * @param charset like "uft-8"
   */
  public GetPage(String u, String charset) {
    msg = new Get(u, charset);
  }

  /**
   * @param u like "/index.php?title=Special:Recentchanges&feed=rss"
   */
  public GetPage(String u) {
    this(u, "utf-8");
  }

  /**
   * @return true if
   */
  @Override
  public boolean hasMoreMessages() {
    final boolean b = hasMore;
    hasMore = false;
    return b;
  }

  /**
   * @return a
   * @see ContentProcessable#getNextMessage()
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

  /**
   * @param s  the returning text
   * @param hm the on any problems with inner browser
   * @return the returning text
   * @see ContentProcessable#processReturningText(String, HttpAction)
   */
  @Override
  public String processReturningText(String s, HttpAction hm) {
    text = s;
    return s;
  }

  /**
   * @return the requested text
   */
  public String getText() {
    return text;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSelfExecuter() {
    return false;
  }

}
