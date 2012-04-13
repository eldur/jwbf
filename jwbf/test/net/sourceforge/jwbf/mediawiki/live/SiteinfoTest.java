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
import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.meta.Siteinfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class SiteinfoTest extends AbstractMediaWikiBotTest {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      GetVersion.class, Siteinfo.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  /**
   * Test get siteinfo on Wikipedia DE.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void siteInfoWikipediaDe() throws Exception {
    String liveUrl = "http://de.wikipedia.org/w/index.php";
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    doTest(bot, Version.DEVELOPMENT);
  }

  /**
   * Test get siteinfo on a MW. Prepare a the wiki, that the siteinfopage is
   * only readable if user is logged in.
   * 
   * <pre>
   * $wgGroupPermissions['*']['read'] = false;
   * </pre>
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void siteInfoMW1x15Blocking() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, false);
    assertEquals(Version.UNKNOWN, bot.getVersion());
    bot.login(getWikiUser(Version.MW1_15), getWikiPass(Version.MW1_15));
    assertEquals(Version.MW1_15, bot.getVersion());
  }

  /**
   * Test get siteinfo on a MW.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void siteInfoMW1x15() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, false);
    doTest(bot, Version.MW1_15);
  }

  /**
   * Test get siteinfo on a MW.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void siteInfoMW1x16() throws Exception {

    bot = getMediaWikiBot(Version.MW1_16, false);
    doTest(bot, Version.MW1_16);
  }

  /**
   * Test last Version.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void siteInfoLastVersion() throws Exception {
    String liveUrl = "http://www.mediawiki.org/w/api.php";
    assumeReachable(liveUrl);
    Version v = Version.getLatest();
    String versionInfoTemplate = "Template:MW stable release number";
    MediaWikiBot bot = new MediaWikiBot(liveUrl);

    Article a = new Article(bot, versionInfoTemplate);
    assertTrue(a.getText() + " should contains " + v.getNumber(), a.getText()
        .contains(v.getNumber()));
  }

  private void doTest(MediaWikiBot bot, Version v) throws Exception {
    assertEquals(v, bot.getVersion());

    GetVersion gv = new GetVersion();
    bot.performAction(gv);

    System.out.println(gv.getBase());
    assertTrue(gv.getBase().length() > 0);

    System.out.println(gv.getCase());
    assertTrue(gv.getCase().length() > 0);

    System.out.println(gv.getGenerator());
    assertTrue(gv.getGenerator().length() > 0);

    System.out.println(gv.getMainpage());
    assertTrue(gv.getMainpage().length() > 0);

    System.out.println(gv.getSitename());
    assertTrue(gv.getSitename().length() > 0);

    Siteinfo si = new Siteinfo();
    bot.performAction(si);
    System.out.println(si.getInterwikis());
    assertTrue("shuld have interwikis", si.getInterwikis().size() > 5);

    System.out.println(si.getNamespaces());
    assertTrue("shuld have namespaces", si.getNamespaces().size() > 15);
    // registerTestedVersion(Siteinfo.class, v); // TODO

  }

  /**
   * Test if useragent ist jwbf.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void testUserAgent() throws Exception {

    HttpBot bot = new HttpBot(""); // XXX does this work?
    String result = bot.getPage(getValue("useragent_url"));
    assertTrue("useragent should contain \"JWBF\"", result.contains("JWBF"));
    assertTrue("useragent should contain actual version",
        result.contains(JWBF.getVersion(getClass())));
  }

}
