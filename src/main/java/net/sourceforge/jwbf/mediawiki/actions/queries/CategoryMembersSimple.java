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

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialization of {@link CategoryMembers} with contains {@link String}s.
 *
 * @author Thomas Stock
 */
public class CategoryMembersSimple extends BaseQuery<String> {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembersSimple.class);

  private final CategoryMembers cm;

  /**
   * @param categoryName like "Buildings" or "Chemical elements" without prefix "Category:" in
   *                     {@link MediaWiki#NS_MAIN}
   */
  public CategoryMembersSimple(MediaWikiBot bot, String categoryName) {
    this(bot, categoryName, MediaWiki.NS_MAIN);

  }

  /**
   * @param categoryName like "Buildings" or "Chemical elements" without prefix "Category:"
   * @param namespaces   for search
   */
  public CategoryMembersSimple(MediaWikiBot bot, String categoryName, int... namespaces) {
    super(bot);
    cm = new CategoryMembersFull(bot, categoryName, namespaces);
  }

  @Override
  protected HttpAction prepareCollection() {
    return cm.prepareCollection();
  }

  @Override
  protected ImmutableList<String> parseArticleTitles(String s) {
    ImmutableList<CategoryItem> categoryItems = cm.parseArticleTitles(s);
    return FluentIterable.from(categoryItems) //
        .transform(CategoryItem.TO_TITLE_STRING_F).toList();
  }

  @Override
  protected Optional<String> parseHasMore(String s) {
    return cm.parseHasMore(s);
  }

  @Override
  public boolean hasNext() {
    return cm.hasNext();
  }

  @Override
  public String next() {
    return cm.next().getTitle();
  }

}
