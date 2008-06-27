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
package net.sourceforge.jwbf.live;

import static org.junit.Assert.assertEquals;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
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
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	
	/**
	 * Test login on Wikipedia.
	 * @throws Exception a
	 */
	@Test
	public final void loginWikipedia1() throws Exception {
		
		bot = new MediaWikiBot(getValue("login_wikipedia1_url"));
		bot.login(getValue("login_wikipedia1_user_valid"), getValue("login_wikipedia1_pass_valid"));
		assertEquals(true, bot.isLoggedIn());
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
		
		bot = new MediaWikiBot(url);
		bot.login(getValue("login_wikipedia1_user_valid"), getValue("login_wikipedia1_pass_valid"));
		assertEquals(true, bot.isLoggedIn());
	}
	/**
	 * Test FAIL login on Wikipedia.
	 * @throws Exception a
	 */
	@Test(expected = ActionException.class) 
	public final void loginWikipedia1Fail() throws Exception {

			bot = new MediaWikiBot(getValue("login_wikipedia1_url"));
			bot.login("Klhjfd", "4sdf");

	}
	
	/**
	 * Test login on Wikipedia. 
	 * @throws Exception a
	 */
	@Test
	public final void loginWikipedia2() throws Exception {
		
		bot = new MediaWikiBot(getValue("login_wikipedia2_url"));
		bot.login(getValue("login_wikipedia2_user_valid"), getValue("login_wikipedia2_pass_valid"));
		assertEquals(true, bot.isLoggedIn());
	}
	
	/**
	 * Test login on Wikipedia .
	 * @throws Exception a
	 */
	@Test
	public final void loginWikipedia2Urlformats() throws Exception {
		String url = getValue("login_wikipedia2_url");
		int lastSlash = url.lastIndexOf("/");
		url = url.substring(0, lastSlash + 1);
		bot = new MediaWikiBot(url);
		bot.login(getValue("login_wikipedia2_user_valid"), getValue("login_wikipedia2_pass_valid"));
		assertEquals(true, bot.isLoggedIn());
	}
	/**
	 * Test FAIL login on english Wikipedia.
	 * @throws Exception a
	 */
	@Test(expected = ActionException.class) 
	public final void loginWikipedia2Fail() throws Exception {

			bot = new MediaWikiBot(getValue("login_wikipedia2_url"));
			bot.login("Klhjfd", "4sdf");

	}

	
	/**
	 * Test login on a Mediawiki. 
	 * @throws Exception a
	 */
	@Test
	public final void loginCustomWiki1() throws Exception {
		
		bot = new MediaWikiBot(getValue("login_customWiki1_url"));
		bot.login(getValue("login_customWiki1_user"), getValue("login_customWiki1_pass"));
		assertEquals(true, bot.isLoggedIn());
	}
	
	/**
	 * Test FAIL login on english Mediawiki. 
	 * @throws Exception a
	 */
	@Test(expected = ActionException.class) 
	public final void loginCustomWiki1Fail() throws Exception {

			bot = new MediaWikiBot(getValue("login_customWiki1_url"));
			bot.login("Klhjfd", "4sdf");

	}
	/**
	 * Test login where the wiki is in a subfolder, like www.abc.com/wiki/index.php
	 * @throws Exception a
	 */
	@Test(expected = ActionException.class) 
	public final void loginCustomWiki1UrlformatsFail() throws Exception {
		
		String url = getValue("login_customWiki2_url");
		int lastSlash = url.lastIndexOf("/");
		url = url.substring(0, lastSlash);
		bot = new MediaWikiBot(url);
		bot.login(getValue("login_customWiki2_user"), getValue("login_customWiki2_pass"));
	}
	

	
	

	
	/**
	 * Test login on a Mediawiki. 
	 * @throws Exception a
	 */
	@Test
	public final void loginCustomWiki1Urlformats() throws Exception {
		
		bot = new MediaWikiBot(getValue("login_customWiki1_url"));
		bot.login(getValue("login_customWiki1_user"), getValue("login_customWiki1_pass"));
		assertEquals(true, bot.isLoggedIn());
		
		String url = getValue("login_customWiki1_url");
		int lastSlash = url.lastIndexOf("/");
		url = url.substring(0, lastSlash + 1);
		
		bot = new MediaWikiBot(url);
		bot.login(getValue("login_customWiki1_user"), getValue("login_customWiki1_pass"));
		assertEquals(true, bot.isLoggedIn());
	}
	
	
	
	/**
	 * Test login on MediaWiki specialchars. Here you should use
	 * logins and passworts with special chars like: "' اي.
	 * @throws Exception a
	 */
	@Test
	public final void loginCustomWiki3() throws Exception {
		
		String user = getValue("login_customWiki3_user");
		String pass = getValue("login_customWiki3_pass");
		bot = new MediaWikiBot(getValue("login_customWiki3_url"));
		bot.login(user, pass);
		
		assertEquals(true, bot.isLoggedIn());
		
		
	}
	
	/**
	 * Test login on a Mediawiki. 
	 * @throws Exception a
	 */
	@Test
	public final void loginCustomWiki2() throws Exception {
		
		bot = new MediaWikiBot(getValue("login_customWiki2_url"));
		bot.login(getValue("login_customWiki2_user"), getValue("login_customWiki2_pass"));
		assertEquals(true, bot.isLoggedIn());
	}
	


}
