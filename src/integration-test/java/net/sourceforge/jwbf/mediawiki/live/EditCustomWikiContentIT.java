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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/** @author Thomas Stock */
public class EditCustomWikiContentIT {

  private MediaWikiBot bot;
  private Random random = new Random(System.currentTimeMillis());

  private List<String> deleteTitles = Lists.newArrayList();

  @Before
  public void setUp() {
    bot = BotFactory.getMediaWikiBot(Version.getLatest(), true);
  }

  @After
  public void cleanUp() {
    for (String title : ImmutableList.copyOf(deleteTitles)) {
      bot.delete(title);
    }
  }

  private String newTitleWithCleanup(String name) {
    String newName = this.getClass().getSimpleName() + "-" + name;
    deleteTitles.add(newName);
    return newName;
  }

  @Test
  public final void contentModifyWithSpaceInTitle() {
    // GIVEN
    String title = newTitleWithCleanup("_Delete 1");
    SimpleArticle sa = new SimpleArticle(title);
    String text = "test " + (random.nextInt(1000));
    sa.setText(text);

    // WHEN
    bot.writeContent(sa);
    String resultText = bot.getArticle(title).getText();

    // THEN
    assertEquals(text, resultText);
  }

  /** Test the read of metadata on english Mediawiki. */
  @Test
  public final void contentModifyDetails() {
    // GIVEN
    String title = newTitleWithCleanup("contentModifyDetails");
    String summary = "clear it";
    SimpleArticle t = new SimpleArticle(title);
    t.setText(getRandom(64));
    t.setEditSummary(summary);
    t.setMinorEdit(true);
    assertEquals(SimpleArticle.newZeroDate(), t.getEditTimestamp());

    // WHEN
    bot.writeContent(t);
    SimpleArticle sa = bot.getArticle(title).getSimpleArticle();

    // THEN
    assertEquals(title, sa.getTitle());
    assertEquals(summary, sa.getEditSummary());
    assertEquals(bot.getUserinfo().getUsername(), sa.getEditor());
    assertEquals(false, sa.isMinorEdit()); // XXX creation can't be a minor edit
    assertNotEquals(SimpleArticle.newZeroDate(), sa.getEditTimestamp());

    // GIVEN
    t.setText(getRandom(64));

    // WHEN
    bot.writeContent(t);
    sa = bot.getArticle(title).getSimpleArticle();

    // THEN
    assertEquals(true, sa.isMinorEdit());
  }
}
