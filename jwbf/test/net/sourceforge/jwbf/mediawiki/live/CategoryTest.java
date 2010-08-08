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
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersFull;
import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersSimple;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class CategoryTest extends LiveTestFather {


  private MediaWikiBot bot = null;
  private static final int COUNT = 60;
  private static final String TESTCATNAME = "TestCat";



  protected void doPreapare(MediaWikiBot bot)
  throws ActionException, ProcessException {
    try {
      SimpleArticle a = new SimpleArticle();

      for (int i = 0; i < COUNT; i++) {
        a.setTitle("CategoryTest" + i);
        a.setText("abc [[Category:" + TESTCATNAME + "]]");
        bot.writeContent(a);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(CategoryMembersSimple.class);
    addInitSupporterVersions(CategoryMembersFull.class);
  }



  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Ignore("for wikipedia a login is required")
  @Test
  public final void categoryWikipediaDe() throws Exception {

    bot = new MediaWikiBot("http://de.wikipedia.org/w/index.php");
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.DEVELOPMENT.equals(bot.getVersion()));

    doTest(bot, "Moose");
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void categoryWikiMW1x09Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
    registerUnTestedVersion(CategoryMembersFull.class, bot.getVersion());
    registerUnTestedVersion(CategoryMembersSimple.class, bot.getVersion());
    doTest(bot);

  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void categoryWikiMW1x10Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
    registerUnTestedVersion(CategoryMembersFull.class, bot.getVersion());
    registerUnTestedVersion(CategoryMembersSimple.class, bot.getVersion());
    doTest(bot);

  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void categoryWikiMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));

    doTest(bot);

  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void categoryWikiMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
    doTest(bot);

  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void categoryWikiMW1x13() throws Exception {

    bot = getMediaWikiBot(Version.MW1_13, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
    doTest(bot);



  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void categoryWikiMW1x14() throws Exception {

    bot = getMediaWikiBot(Version.MW1_14, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
    doTest(bot);
  }


  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void categoryWikiMW1x15() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
    doTest(bot);
  }

  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test
  public final void categoryWikiMW1x16() throws Exception {

    bot = getMediaWikiBot(Version.MW1_16, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
    doTest(bot);
  }

  private void doTest(MediaWikiBot bot) throws ActionException, ProcessException {
    doTest(bot, TESTCATNAME);
  }

  private void doTest(MediaWikiBot bot, String catname) throws ActionException, ProcessException {

    Collection<String> compare1 = new Vector<String>();
    Collection<CategoryItem> compare2 = new Vector<CategoryItem>();
    Iterator<String> is = new CategoryMembersSimple(bot, catname).iterator();
    int i = 0;
    boolean notEnough = true;
    while (is.hasNext()) {
      is.next();
      i++;
      if (i > 55) {
        notEnough = false;
        break;
      }
    }
    if (notEnough) {
      System.err.println("begin prepare");
      doPreapare(bot);
    }


    is = new CategoryMembersSimple(bot, catname).iterator();
    i = 0;
    while (is.hasNext()) {
      String x = is.next();
      if (!compare1.contains(x)) {
        compare1.add(x);
      } else {
        fail(x + " alredy in collection");
      }

      i++;
      if (i > 55) {
        break;
      }
    }
    assertTrue("i is: " + i , i > 50);
    registerTestedVersion(CategoryMembersSimple.class, bot.getVersion());

    Iterator<CategoryItem> cit = new CategoryMembersFull(bot, catname).iterator();
    i = 0;
    while (cit.hasNext()) {
      CategoryItem x = cit.next();
      if (!compare2.contains(x)) {
        compare2.add(x);
      } else {
        fail(x.getTitle() + " alredy in collection");
      }
      i++;
      if (i > 55) {
        break;
      }
    }
    assertTrue("i is: " + i , i > 50);

    registerTestedVersion(CategoryMembersFull.class, bot.getVersion());
  }

}
