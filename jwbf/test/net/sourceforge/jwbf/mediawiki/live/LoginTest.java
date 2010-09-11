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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLoginOld;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
/**
 * Test Login.
 * @author Thomas Stock
 *
 *
 */
public class LoginTest extends LiveTestFather {

  private MediaWikiBot bot = null;

  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
  }

  /**
   * Test login on Wikipedia.
   * @throws Exception a
   */
  @Test
  public final void loginWikipedia1() throws Exception {
    String url = getValue("login_wikipedia1_url");
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    bot.login(getValue("login_wikipedia1_user_valid"), getValue("login_wikipedia1_pass_valid"));
    assertTrue(bot.isLoggedIn());
  }

  /**
   * Test login on Wikipedia.
   * @throws Exception a
   */
  @Test
  public final void loginWikipedia1Urlformats() throws Exception {

    String url = getValue("login_wikipedia1_url");
    int lastSlash = url.lastIndexOf("/");
    url = url.substring(0, lastSlash + 1);
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    bot.login(getValue("login_wikipedia1_user_valid"), getValue("login_wikipedia1_pass_valid"));
    assertTrue(bot.isLoggedIn());
  }
  /**
   * Test FAIL login on Wikipedia.
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void loginWikipedia1Fail() throws Exception {

    String url = getValue("login_wikipedia1_url");
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    bot.login("Klhjfd", "4sdf");
    assertFalse("Login does not exist", bot.isLoggedIn());

  }

  /**
   * Test login on a Mediawiki.
   * @throws Exception a
   */
  @Test
  public final void loginWikiMW1x09() throws Exception {
    String url = getValue("wikiMW1_09_url");
    assertTrue("shuld end with .php" , url.endsWith(".php"));
    bot = new MediaWikiBot(url);
    bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
    assertTrue(bot.isLoggedIn());
    registerTestedVersion(PostLoginOld.class, bot.getVersion());
  }

  /**
   * Test FAIL login on Mediawiki.
   * TODO change exception test, should fail if no route to test host
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void loginWikiMW1x09Fail() throws Exception {
    bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
    bot.login("Klhjfd", "4sdf");

  }
  /**
   * Test login where the wiki is in a subfolder, like www.abc.com/wiki .
   * @throws Exception a
   */
  @Test(expected = MalformedURLException.class)
  public final void loginWikiMW1x09UrlformatsFail() throws Exception {

    String url = getValue("wikiMW1_09_url");
    int lastSlash = url.lastIndexOf("/");
    url = url.substring(0, lastSlash);
    assertFalse("shuld not end with .php" , url.endsWith(".php"));
    bot = new MediaWikiBot(url);
    bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
  }

  /**
   * Test login on a Mediawiki.
   * @throws Exception a
   */
  @Test
  public final void loginWikiMW1x09Urlformats() throws Exception {
    String url = getValue("wikiMW1_09_url");
    int lastSlash = url.lastIndexOf("/");
    url = url.substring(0, lastSlash + 1);
    assertFalse("shuld not end with .php" , url.endsWith(".php"));
    bot = new MediaWikiBot(url);
    bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
    assertTrue(bot.isLoggedIn());
  }


  /**
   * Test invalid installation of MW.
   * TODO change exception test, should fail if no route to test host
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void installationDefunct() throws Exception {
    String url = getValue("wikiMWinvalid_url");
    bot = new MediaWikiBot(url);

    bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
  }

  /**
   * Test invalid installation of MW.
   * TODO change exception test, should fail if no route to test host
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void conncetionProblem() throws Exception {
    String url = "http://www.google.com/invalidWiki/";
    bot = new MediaWikiBot(url);

    bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));

  }

  /**
   * Test login on a Mediawiki.
   * @throws Exception a
   */
  @Test
  public final void loginWikiMWLast() throws Exception {

    bot = getMediaWikiBot(Version.getLast(), false);
    bot.login(getWikiUser(Version.getLast()), getWikiPass(Version.getLast()));
    assertTrue(bot.isLoggedIn());
    registerTestedVersion(PostLoginOld.class, bot.getVersion());
  }
  /**
   * Login on last MW with SSL. TODO unignore
   * @throws Exception a
   */
  @Ignore
  @Test
  public final void loginWikiMWLastSSL() throws Exception {
    URL u = new URL(getWikiUrl(Version.getLast()).replace("http", "https"));

    assertEquals("https", u.getProtocol());
    bot = new MediaWikiBot(u);
    bot.login(getWikiUser(Version.getLast()), getWikiPass(Version.getLast()));
    assertTrue(bot.isLoggedIn());
  }

}
