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
package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValueOrSkip;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public class EditCustomWikiContentTest {

  private MediaWikiBot bot;
  private Random random = new Random(System.currentTimeMillis());

  @Before
  public void setUp() {
    bot = BotFactory.getMediaWikiBot(Version.getLatest(), true);
  }

  /**
   * Test content modification.
   */
  @Test
  public final void contentModify() {
    String title = getValueOrSkip("test_live_article");
    SimpleArticle sa;
    sa = new SimpleArticle(title);
    sa.setText(getRandom(64));
    try {
      bot.writeContent(sa);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    sa = bot.getArticle(title).getSimpleArticle();
    String text = "test " + (random.nextInt(1000));
    sa.setText(text);
    bot.writeContent(sa);
    assertEquals(text, bot.getArticle(title).getText());
  }

  @Test
  public final void contentModifyWithSpacetitle() {
    String title = "Delete 1";
    SimpleArticle sa;
    sa = new SimpleArticle(title);
    sa.setText(getRandom(64));
    bot.writeContent(sa);
    sa = bot.getArticle(title).getSimpleArticle();
    String text = "test " + (random.nextInt(1000));
    sa.setText(text);
    bot.writeContent(sa);
    assertEquals(text, bot.getArticle(title).getText());
  }

  /**
   * Test the read of metadata on english Mediawiki.
   */
  @Test
  public final void contentModifyDetails() {
    String title = getValueOrSkip("test_live_article");
    String summary = "clear it";
    SimpleArticle t = new SimpleArticle(title);
    t.setText(getRandom(64));
    t.setEditSummary(summary);
    t.setMinorEdit(true);
    try {
      bot.writeContent(t);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    SimpleArticle sa = bot.getArticle(title).getSimpleArticle();
    assertEquals(title, sa.getTitle());
    assertEquals(summary, sa.getEditSummary());
    assertEquals(bot.getUserinfo().getUsername(), sa.getEditor());
    assertEquals(true, sa.isMinorEdit());
  }

  @Test
  public final void getTimestamp() {
    String label = getValueOrSkip("test_live_article");
    ArticleMeta sa;

    sa = bot.getArticle(label);

    assertTrue(sa.getEditTimestamp().getTime() > 1000);
  }

}
