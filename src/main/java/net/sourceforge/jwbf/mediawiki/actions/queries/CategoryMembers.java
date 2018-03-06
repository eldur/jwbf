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

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.internal.Checked;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;

/**
 * A abstract action class using the MediaWiki-api's "list=categorymembers ".
 *
 * @author Thomas Stock
 * @see <a href= "http://www.mediawiki.org/wiki/API:Query_-_Lists#categorymembers_.2F_cm">API
 *     documentation</a>
 */
abstract class CategoryMembers extends BaseQuery<CategoryItem> {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembers.class);

  /** constant value for the bllimit-parameter. * */
  protected static final int LIMIT = 50;

  final String categoryName;
  private final String namespaceStr;
  final ImmutableList<Integer> namespace;

  protected CategoryMembers(
      MediaWikiBot bot, String categoryName, ImmutableList<Integer> namespaces) {
    super(bot);
    this.namespace = Checked.nonNull(namespaces, "namespaces");
    this.namespaceStr = MWAction.createNsString(namespaces);
    this.categoryName = Checked.nonNull(categoryName, "categoryName").replace(" ", "_");
  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @return a
   */
  Get generateFirstRequest() {
    return newRequestBuilder() //
        .buildGet();
  }

  /**
   * generates the next MediaWiki-request (GetMethod) and adds it to msgs.
   *
   * @param cmcontinue the value for the blcontinue parameter, null for the generation of the
   *     initial request
   * @return a
   */
  Get generateContinueRequest(String cmcontinue) {
    return newRequestBuilder() //
        .param("cmcontinue", MediaWiki.urlEncode(cmcontinue)) //
        .buildGet();
  }

  /**
   * gets the information about a follow-up page from a provided api response. If there is one, a
   * new request is added to msgs by calling generateRequest.
   *
   * @param xml text for parsing
   */
  @Override
  public Optional<String> parseHasMore(final String xml) {
    return parseXmlHasMore(xml, "categorymembers", "cmcontinue", "cmcontinue");
  }

  /**
   * picks the article name from a MediaWiki api response.
   *
   * @param xml text for parsing
   */
  @Override
  public ImmutableList<CategoryItem> parseElements(String xml) {
    return parseArticles(xml, toCategoryItem());
  }

  private NonnullFunction<XmlElement, CategoryItem> toCategoryItem() {
    return new NonnullFunction<XmlElement, CategoryItem>() {
      @Nonnull
      @Override
      protected CategoryItem applyNonnull(@Nonnull XmlElement input) {
        String title = input.getAttributeValueNonNull("title");
        int namespace = Integer.parseInt(input.getAttributeValueNonNull("ns"));
        int pageId = Integer.parseInt(input.getAttributeValueNonNull("pageid"));
        return new CategoryItem(title, namespace, pageId);
      }
    };
  }

  <T> ImmutableList<T> parseArticles(String xml, NonnullFunction<XmlElement, T> f) {
    Optional<XmlElement> child = XmlConverter.getChildOpt(xml, "query", "categorymembers");
    if (child.isPresent()) {
      List<XmlElement> children = child.get().getChildren();
      return FluentIterable.from(children).transform(f).toList();
    } else {
      return ImmutableList.of();
    }
  }

  private RequestBuilder newRequestBuilder() {
    ApiRequestBuilder requestBuilder = new ApiRequestBuilder();
    if (namespaceStr.length() > 0) {
      requestBuilder.param("cmnamespace", MediaWiki.urlEncode(namespaceStr));
    }

    return requestBuilder //
        .action("query") //
        .formatXml() //
        .paramNewContinue(bot().getVersion()) //
        .param("list", "categorymembers") //
        .param("cmlimit", LIMIT) //
        .param("cmtitle", "Category:" + MediaWiki.urlEncode(categoryName)) //
    // TODO: do not add Category: - instead, change other methods' descs (e.g.
    // in MediaWikiBot)
    ;
  }
}
