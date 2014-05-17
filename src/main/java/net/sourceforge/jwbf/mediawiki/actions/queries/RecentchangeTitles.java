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
package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.core.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.extractXml.Element;
import net.sourceforge.jwbf.extractXml.XmlConverter;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets a list of pages recently changed, ordered by modification timestamp. Parameters: rcfrom (paging timestamp), rcto
 * (flt), rcnamespace (flt), rcminor (flt), rcusertype (dflt=not|bot), rcdirection (dflt=older), rclimit (dflt=10,
 * max=500/5000) F api.php ? action=query & list=recentchanges - List last 10 changes
 *
 * @author Thomas Stock
 */
public class RecentchangeTitles extends TitleQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(RecentchangeTitles.class);

  /**
   * value for the bllimit-parameter. *
   */
  private static final int limit = 50;

  private final MediaWikiBot bot;

  private final int[] namespaces;

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param namespace the namespace(s) that will be searched for links, as a string of numbers separated by '|'; if null, this
   *                  parameter is omitted
   * @param rcstart   timestamp
   */
  private HttpAction generateRequest(int[] namespace, String rcstart) {

    RequestBuilder requestBuilder = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("list", "recentchanges") //
        .param("rclimit", limit) //
        ;
    if (namespace != null) {
      requestBuilder.param("rcnamespace", MediaWiki.encode(MWAction.createNsString(namespace)));
    }
    if (rcstart.length() > 0) {
      requestBuilder.param("rcstart", rcstart);
    }

    return requestBuilder.buildGet();

  }

  private HttpAction generateRequest(int[] namespace) {
    return generateRequest(namespace, "");
  }

  /**
   *
   */
  public RecentchangeTitles(MediaWikiBot bot, int... ns) {
    super(bot);
    namespaces = ns;
    this.bot = bot;

  }

  /**
   *
   */
  public RecentchangeTitles(MediaWikiBot bot) {
    this(bot, MediaWiki.NS_ALL);
  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param s text for parsing
   */
  @Override
  protected ImmutableList<String> parseArticleTitles(String s) {
    Element root = XmlConverter.getRootElement(s);
    List<String> titleCollection = Lists.newArrayList();
    findContent(root, titleCollection);
    return ImmutableList.copyOf(titleCollection);

  }

  private void findContent(final Element root, List<String> titleCollection) {

    Iterator<Element> el = root.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();
      if (element.getQualifiedName().equalsIgnoreCase("rc")) {
        titleCollection.add(MediaWiki.decode(element.getAttributeValue("title")));
        setNextPageInfo(element.getAttributeValue("timestamp"));
      } else {
        findContent(element, titleCollection);
      }

    }
  }

  @Override
  protected HttpAction prepareCollection() {
    if (hasNextPageInfo()) {
      return generateRequest(namespaces, getNextPageInfo());
    } else {
      return generateRequest(namespaces);
    }

  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new RecentchangeTitles(bot, namespaces);
  }

  @Override
  protected String parseHasMore(String s) {
    return "";
  }

}
