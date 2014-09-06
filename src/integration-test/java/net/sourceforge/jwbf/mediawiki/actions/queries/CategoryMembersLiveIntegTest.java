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

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryMembersLiveIntegTest extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(CategoryMembersLiveIntegTest.class);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public CategoryMembersLiveIntegTest(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  private static final int COUNT = 60;
  private static final String TESTCATNAME = "TestCat";

  @Test
  public void doTest() {
    // GIVEN / WHEN
    int limitSize = 55;
    final ImmutableList<String> categoryTitles = getOrCreateCategoryTitles(limitSize);
    ImmutableList<CategoryItem> categoryItems = //
        copyWithoutDuplicatesOf(new CategoryMembersFull(bot(), TESTCATNAME), limitSize);

    // THEN
    assertEquals(limitSize, categoryItems.size());
    assertEquals(limitSize, categoryTitles.size());
    GAssert.assertEquals(categoryTitles, //
        FluentIterable.from(categoryItems) //
            .transform(CategoryItem.toTitleStringFunction()) //
            .toList());
  }

  private void doPreapare() {
    log.info("begin prepare");
    SimpleArticle a = new SimpleArticle();

    for (int i = 0; i < COUNT; i++) {
      a.setTitle("CategoryTest" + i);
      a.setText("abc [[Category:" + TESTCATNAME + "]]");
      bot().writeContent(a);
    }
  }

  private ImmutableList<String> getOrCreateCategoryTitles(int limitSize) {
    CategoryMembersSimple categories = new CategoryMembersSimple(bot(), TESTCATNAME);
    ImmutableList<String> initialCategories = copyWithoutDuplicatesOf(categories.lazy(), limitSize);
    if (initialCategories.size() < limitSize) {
      doPreapare();
      return copyWithoutDuplicatesOf(new CategoryMembersSimple(bot(), TESTCATNAME), limitSize);
    } else {
      return initialCategories;
    }
  }

  public static <T> ImmutableList<T> copyWithoutDuplicatesOf(Iterable<T> elements,
      int limitSize) {
    ImmutableList<T> ts = copyOfWithLimit(elements, limitSize);
    GAssert.assertEquals(ts, Sets.newHashSet(ts));
    return ts;
  }

  public static <T> ImmutableList<T> copyOfWithLimit(Iterable<T> elements, int limitSize) {
    return ImmutableList.copyOf(Iterables.limit(elements, limitSize));
  }

}
