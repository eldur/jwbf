/*
 * Copyright 2016 Thomas Stock, Marco Ammon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * Gets a list of pages with unreviewed changes.
 *
 * @author Thomas Stock, Marco Ammon
 */
public class OldreviewedPagesTitles extends BaseQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(RecentchangeTitles.class);
  /** value for the orlimit-parameter. */
  private static final int LIMIT = 50;

  private final MediaWikiBot bot;
  private final int[] namespaces;

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param namespace the namespace(s) that will be searched for links, as a string of numbers
   *     separated by '|'; if null, this parameter is omitted
   * @param orstart Start listing at this timestamp
   * @param orend Stop listing at this timestamp
   */
  private HttpAction generateRequest(int[] namespace, String orstart, String orend) {

    RequestBuilder requestBuilder =
        new ApiRequestBuilder() //
            .action("query") //
            .formatXml() //
            .param("list", "oldreviewedpages") //
            .param("orlimit", LIMIT) //
        ;
    if (namespace != null) {
      String ornamespace = MediaWiki.urlEncode(MWAction.createNsString(namespace));
      requestBuilder.param("ornamespace", ornamespace);
    }
    if (orstart.length() > 0) {
      requestBuilder.param("orstart", orstart);
    }
    if (orend.length() > 0) {
      requestBuilder.param("orend", orend);
    }

    return requestBuilder.buildGet();
  }

  private HttpAction generateRequest(int[] namespace, String orstart) {
    return generateRequest(namespace, orstart, "");
  }

  private HttpAction generateRequest(int[] namespace) {
    return generateRequest(namespace, "", "");
  }

  /** */
  public OldreviewedPagesTitles(MediaWikiBot bot, int... ns) {
    super(bot);
    namespaces = ns;
    this.bot = bot;
  }

  /** */
  public OldreviewedPagesTitles(MediaWikiBot bot) {
    this(bot, MediaWiki.NS_ALL);
  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param s text for parsing
   */
  @Override
  protected ImmutableList<String> parseElements(String s) {
    XmlElement root = XmlConverter.getRootElement(s);
    List<String> titleCollection = Lists.newArrayList();
    findContent(root, titleCollection);
    return ImmutableList.copyOf(titleCollection);
  }

  private void findContent(final XmlElement root, List<String> titleCollection) {

    for (XmlElement xmlElement : root.getChildren()) {
      if (xmlElement.getQualifiedName().equalsIgnoreCase("p")) {
        titleCollection.add(MediaWiki.htmlUnescape(xmlElement.getAttributeValue("title")));
      } else {
        findContent(xmlElement, titleCollection);
      }
    }
  }

  @Override
  protected HttpAction prepareNextRequest() {
    Optional<String> orcontinue = nextPageInfoOpt();
    if (orcontinue.isPresent()) {
      return generateRequest(namespaces, orcontinue.get());
    } else {
      return generateRequest(namespaces);
    }
  }

  @Override
  protected Iterator<String> copy() {
    return new OldreviewedPagesTitles(bot, namespaces);
  }

  @Override
  protected Optional<String> parseHasMore(final String xml) {
    return parseXmlHasMore(xml, "oldreviewedpages", "orstart", "orstart");
  }
}
