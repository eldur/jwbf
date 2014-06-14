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
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiPass;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteinfoTest extends AbstractMediaWikiBotTest {

  private static final Logger log = LoggerFactory.getLogger(SiteinfoTest.class);

  /**
   * Test get siteinfo on Wikipedia DE.
   */
  @Ignore("do we need this?")
  @Test
  public final void siteInfoWikipediaDe() {
    String liveUrl = getWikipediaDeUrl();
    bot = new MediaWikiBot(liveUrl);
    // doTest(bot, Version.DEVELOPMENT);
  }

  /**
   * Test get siteinfo on a MW. Prepare a the wiki, that the siteinfopage is only readable if user
   * is logged in.
   * <pre>
   * $wgGroupPermissions['*']['read'] = false;
   * </pre>
   */
  @Test
  public final void siteInfoMW1x15Blocking() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, false);
    assertEquals(Version.UNKNOWN, bot.getVersion());
    bot.login(getWikiUser(Version.MW1_15), getWikiPass(Version.MW1_15));
    assertEquals(Version.MW1_15, bot.getVersion());
  }

  @Test
  @Ignore // FIXME mediawikirelease is now at 1.23
  public final void siteInfoLastVersion() {
    String liveUrl = "http://www.mediawiki.org/w/api.php";
    assumeReachable(liveUrl);
    Version v = Version.getLatest();
    String versionInfoTemplate = "Template:MW stable release number";
    MediaWikiBot bot = new MediaWikiBot(liveUrl);

    Article a = new Article(bot, versionInfoTemplate);
    assertTrue(a.getText() + " should contains " + v.getNumber(),
        a.getText().contains(v.getNumber()));
  }

}
