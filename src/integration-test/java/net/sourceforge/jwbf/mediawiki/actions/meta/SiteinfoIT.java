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
package net.sourceforge.jwbf.mediawiki.actions.meta;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiPass;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.AbstractMediaWikiBotIT;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteinfoIT extends AbstractMediaWikiBotIT {

  private static final Logger log = LoggerFactory.getLogger(SiteinfoIT.class);

  /**
   * Test get siteinfo on Wikipedia DE.
   */
  @Test
  public final void siteInfoWikipediaDe() {
    // GIVEN
    String liveUrl = getWikipediaDeUrl();
    MediaWikiBot wikiBot = BotFactory.newWikimediaBot(liveUrl, getClass());

    // WHEN
    Siteinfo siteinfo = wikiBot.getSiteinfo();

    // THEN
    assertEquals(Version.DEVELOPMENT, siteinfo.getVersion());
  }

  /**
   * Test get siteinfo on a MW. Prepare a the wiki, that the siteinfopage is only readable if user
   * is logged in.
   * <pre>
   * $wgGroupPermissions['*']['read'] = false;
   * </pre>
   */
  @Test
  public final void siteInfoBlocking() {
    // GIVEN
    Version version = Version.MW1_19;
    bot = getMediaWikiBot(version, false);
    assertEquals(Version.UNKNOWN, bot.getVersion());

    // WHEN
    bot.login(getWikiUser(version), getWikiPass(version));

    // THEN
    assertEquals(version, bot.getVersion());
  }

  @Test
  public final void siteInfoLastVersion() {
    // GIVEN
    String liveUrl = "http://www.mediawiki.org/w/api.php";
    assumeReachable(liveUrl);
    String versionInfoTemplate = "Template:MW stable release number";
    MediaWikiBot bot = BotFactory.newWikimediaBot(liveUrl, getClass());

    // WHEN
    Article a = new Article(bot, versionInfoTemplate);

    // THEN
    Version latestVersion = Version.getLatest();
    assertTrue(a.getText() + " should contains " + latestVersion.getNumber(),
        a.getText().contains(latestVersion.getNumber()));
  }

}
