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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A abstract action class using the MediaWiki-api's "list=categorymembers ".
 *
 * @author Thomas Stock
 * @see <a href= "http://www.mediawiki.org/wiki/API:Query_-_Lists#categorymembers_.2F_cm">API
 * documentation</a>
 */
abstract class CategoryMembers extends BaseQuery<CategoryItem> {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembers.class);

  // TODO do not work with patterns
  private static final Pattern CATEGORY_PATTERN =
      Pattern.compile("<cm pageid=\"(.*?)\" ns=\"(.*?)\" title=\"(.*?)\" />");

  private static final Pattern CONTINUE_PATTERN = Pattern.compile("<query-continue>.*?" + //
      "<categorymembers *cmcontinue=\"([^\"]*)\" */>" + //
      ".*?</query-continue>", Pattern.DOTALL | Pattern.MULTILINE);

  /**
   * constant value for the bllimit-parameter. *
   */
  protected static final int LIMIT = 50;

  final MediaWikiBot bot;
  private final RequestGenerator requestBuilder;

  final String categoryName;
  private final String namespaceStr;
  final ImmutableList<Integer> namespace;

  protected CategoryMembers(MediaWikiBot bot, String categoryName, int[] namespaces) {
    this(bot, categoryName, ImmutableList.copyOf(Ints.asList(namespaces)));
  }

  protected CategoryMembers(MediaWikiBot bot, String categoryName,
      ImmutableList<Integer> namespaces) {
    super(bot);
    this.bot = bot;
    this.namespace = namespaces;
    namespaceStr = MWAction.createNsString(namespaces);
    this.categoryName = categoryName.replace(" ", "_");
    requestBuilder = new RequestGenerator();

  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @return a
   */
  protected final Get generateFirstRequest() {
    return requestBuilder.first(categoryName);
  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param cmcontinue the value for the blcontinue parameter, null for the generation of the
   *                   initial request
   * @return a
   */
  protected Get generateContinueRequest(String cmcontinue) {
    return requestBuilder.continiue(cmcontinue);
  }

  /**
   * gets the information about a follow-up page from a provided api response. If there is one, a
   * new request is added to msgs by calling generateRequest.
   *
   * @param s text for parsing
   */
  @Override
  public Optional<String> parseHasMore(final String s) {

    Matcher m = CONTINUE_PATTERN.matcher(s);

    if (m.find()) {
      return Optional.fromNullable(m.group(1));
    } else {
      return Optional.absent();
    }
  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param xml text for parsing
   */
  @Override
  public ImmutableList<CategoryItem> parseArticleTitles(String xml) {

    XmlConverter.failOnError(xml);
    Matcher m = CATEGORY_PATTERN.matcher(xml);

    ImmutableList.Builder<CategoryItem> listBuilder = ImmutableList.builder();
    while (m.find()) {
      String title = m.group(3);
      int namespace = Integer.parseInt(m.group(2));
      int pageId = Integer.parseInt(m.group(1));
      CategoryItem categoryItem = new CategoryItem(title, namespace, pageId);
      listBuilder.add(categoryItem);
    }
    return listBuilder.build();
  }

  protected class RequestGenerator {

    private static final String CMTITLE = "cmtitle";

    RequestGenerator() {

    }

    Get continiue(String cmcontinue) {
      return newRequestBuilder() //
          .param("cmcontinue", MediaWiki.urlEncode(cmcontinue)) //
          .param(CMTITLE, "Category:" + MediaWiki.urlEncode(categoryName)) //
              // TODO: do not add Category: - instead, change other methods' descs (e.g.
              // in MediaWikiBot)
          .buildGet();
    }

    private RequestBuilder newRequestBuilder() {
      ApiRequestBuilder requestBuilder = new ApiRequestBuilder();
      if (namespaceStr.length() > 0) {
        requestBuilder.param("cmnamespace", MediaWiki.urlEncode(namespaceStr));
      }

      return requestBuilder //
          .action("query") //
          .formatXml() //
          .param("list", "categorymembers") //
          .param("cmlimit", LIMIT) //
          ;
    }

    Get first(String categoryName) {
      return newRequestBuilder() //
          .param(CMTITLE, "Category:" + MediaWiki.urlEncode(categoryName)) //
          .buildGet();
    }

  }

}
