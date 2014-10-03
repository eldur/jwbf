/*
 * Copyright 2007 Tobias Knerr.
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
 * Tobias Knerr
 *
 */
package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.Checked;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * action class using the MediaWiki-api's "list=backlinks".
 *
 * @author Thomas Stock
 * @author Tobias Knerr
 * @since JWBF 1.1
 */
public class BacklinkTitles extends BaseQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(BacklinkTitles.class);

  private final int backlinksPerRequestLimit;

  private final String articleName;

  private final MediaWikiBot bot;
  private final RedirectFilter redirectFilter;
  private final ImmutableList<Integer> namespaces;

  BacklinkTitles(MediaWikiBot bot, String articleName, int backlinksPerRequestLimit,
      RedirectFilter redirectFilter, ImmutableList<Integer> namespaces) {
    super(bot);
    this.backlinksPerRequestLimit = backlinksPerRequestLimit;

    this.bot = Checked.nonNull(bot, "bot");
    this.articleName = Checked.nonNull(articleName, "articleName");
    this.redirectFilter = Checked.nonNull(redirectFilter, "redirectFilter");
    this.namespaces = namespaces;

  }

  /**
   * The public constructor. It will have a MediaWiki-request generated, which is then added to
   * msgs. When it is answered, the method processAllReturningText will be called (from outside this
   * class).
   *
   * @param articleName    the title of the article, != null
   * @param namespaces     the namespace(s) that will be searched for links, as a string of numbers
   *                       separated by '|'; if null, this parameter is omitted. See for e.g. {@link
   *                       MediaWiki#NS_ALL}.
   * @param redirectFilter filter that determines how to handle redirects, must be all for MW
   *                       versions before 1.11; != null
   */
  public BacklinkTitles(MediaWikiBot bot, String articleName, RedirectFilter redirectFilter,
      int... namespaces) {
    this(bot, articleName, 50, redirectFilter, MWAction.nullSafeCopyOf(namespaces));
  }

  public BacklinkTitles(MediaWikiBot bot, String articleName) {
    this(bot, articleName, RedirectFilter.all);

  }

  @Override
  protected Iterator<String> copy() {
    return new BacklinkTitles(bot, articleName, backlinksPerRequestLimit, redirectFilter,
        namespaces);
  }

  /**
   * gets the information about a follow-up page from a provided api response. If there is one, the
   * information for the next page parameter is added to the nextPageInfo field.
   *
   * @param xml text for parsing
   */
  @Override
  protected Optional<String> parseHasMore(final String xml) {
    return parseXmlHasMore(xml, "backlinks", "blcontinue", "blcontinue");

  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param xml text for parsing
   */
  @Override
  protected ImmutableList<String> parseArticleTitles(String xml) {
    List<XmlElement> backlinks = XmlConverter.getChild(xml, "query", "backlinks").getChildren("bl");
    ImmutableList.Builder<String> titleCollection = ImmutableList.builder();

    for (XmlElement backlink : backlinks) {
      titleCollection.add(backlink.getAttributeValue("title"));
    }
    return titleCollection.build();

  }

  private RequestBuilder newRequestBuilder(String title, RedirectFilter redirectFilter,
      ImmutableList<Integer> namespaces) {

    RequestBuilder builder = new ApiRequestBuilder() //
        .action("query") //
        .paramNewContinue(bot.getVersion()) //
        .formatXml() //
        .param("list", "backlinks") //
        .param("bllimit", backlinksPerRequestLimit) //
        .param("bltitle", MediaWiki.urlEncode(title)) //
        .param("blfilterredir", MediaWiki.urlEncode(redirectFilter.toString()));

    if (!namespaces.isEmpty()) {
      builder.param("blnamespace", MediaWiki.urlEncode(MWAction.createNsString(namespaces)));
    }

    return builder;

  }

  private Get newInitialRequest(String articleName, RedirectFilter redirectFilter,
      ImmutableList<Integer> namespace) {
    RequestBuilder requestBuilder = newRequestBuilder(articleName, redirectFilter, namespace);

    return requestBuilder.buildGet();
  }

  private Get newContinueRequest(String articleName, RedirectFilter redirectFilter,
      ImmutableList<Integer> namespaces, String blcontinue) {
    return newRequestBuilder(articleName, redirectFilter, namespaces) //
        .param("blcontinue", MediaWiki.urlEncode(blcontinue)) //
        .buildGet();
  }

  @Override
  protected HttpAction prepareCollection() {
    if (hasNextPageInfo()) {
      return newContinueRequest(articleName, redirectFilter, namespaces, getNextPageInfo());
    } else {
      return newInitialRequest(articleName, redirectFilter, namespaces);
    }
  }

}
