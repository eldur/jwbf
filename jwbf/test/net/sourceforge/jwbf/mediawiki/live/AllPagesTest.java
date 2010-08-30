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


import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class AllPagesTest extends LiveTestFather {


  private MediaWikiBot bot = null;
  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(AllPageTitles.class);
  }




  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikipediaDe() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php";
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    doTest(bot, false);
  }


  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x09() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x10() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void allPagesWikiMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doTest(bot, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
  }

  private void doTest(MediaWikiBot bot, boolean isFullTest) throws Exception {
    AllPageTitles gat = new AllPageTitles(bot, null, null, RedirectFilter.all, MediaWiki.NS_MAIN);

    SimpleArticle sa;
    String testText = getRandom(255);


    Collection<String> specialChars = getSpecialChars();
    if (isFullTest) {
      try {
        for (String title : specialChars) {
          sa = new SimpleArticle(title);
          sa.setText(testText);
          bot.writeContent(sa);
        }
      } catch (ActionException e) {
        boolean found = false;
        for (char ch : MediaWikiBot.INVALID_LABEL_CHARS) {
          if (e.getMessage().contains(ch + "")) {
            found = true;
            break;
          }
        }
        assertTrue("should be a know invalid char",  found);
      }
    }

    Iterator<String> is = gat.iterator();
    int i = 0;
    while (is.hasNext()) {
      String nx = is.next();
      if (isFullTest)
        specialChars.remove(nx);
      i++;
      if (i > 55) {
        break;
      }
    }



    if (isFullTest) {
      for (char c : MediaWikiBot.INVALID_LABEL_CHARS) {
        specialChars.remove(c + "");
      }
      if (!specialChars.isEmpty()) {
        while (is.hasNext() || !specialChars.isEmpty()) {
          specialChars.remove(is.next());
        }
      }

      Assert.assertTrue("tc sould be empty but is: " + specialChars,
          specialChars.isEmpty());
    }
    Assert.assertTrue("i is: " + i, i > 50);
    registerTestedVersion(AllPageTitles.class, bot.getVersion());
  }


}

