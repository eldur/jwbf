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

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.misc.GetRendering;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class RenderingTest extends AbstractMediaWikiBotTest {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      GetRendering.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void getRenderingWikipediaDe() throws Exception {
    String liveUrl = "http://de.wikipedia.org/w/index.php";
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    doTest(bot);
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = ActionException.class)
  public final void getRenderingPerformManual() throws Exception {
    String liveUrl = "http://de.wikipedia.org/w/index.php";
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    GetRendering r = new GetRendering(bot, "bert");
    bot.performAction(r);
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void getRenderingMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void getRenderingMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doTest(bot);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_16.equals(bot.getVersion()));
  }

  private void doTest(MediaWikiBot bot) throws Exception {
    GetRendering r = new GetRendering(bot, "bert");
    Assert.assertEquals("<p>bert</p>", r.getHtml());

    // TODO more tests
    // FIXME looks strange, because we have 3 faild actions
  }
}
