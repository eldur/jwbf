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


import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.misc.GetRendering;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class RenderingTest extends LiveTestFather {


  private MediaWikiBot bot = null;
  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(GetRendering.class);

  }


  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void getRenderingWikipediaDe() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php";
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    doTest(bot);
  }

  /**
   * 
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void getRenderingPerformManual() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php";
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    GetRendering r = new GetRendering(bot, "bert");
    bot.performAction(r);
  }

  /**
   * 
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void getRenderingMW1x09Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
  }
  /**
   * 
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void getRenderingMW1x10Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void getRenderingMW1x11Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void getRenderingMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
  }
  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void getRenderingMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);
    Assert.assertEquals("Wrong Wiki Version " + bot.getVersion(), bot.getVersion(), Version.MW1_13);
    doTest(bot);

  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void getRenderingMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void getRenderingMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void getRenderingMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
  }

  private void doTest(MediaWikiBot bot) throws Exception {
    GetRendering r = new GetRendering(bot, "bert");
    Assert.assertEquals("<p>bert</p>", r.getHtml());

    registerTestedVersion(GetRendering.class, bot.getVersion());
    // TODO more tests
    // FIXME looks strange, because we have 3 faild actions
  }
}

