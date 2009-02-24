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
package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;

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

	private MediaWikiAdapterBot bot = null;
	
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
		
		bot = new MediaWikiAdapterBot(getValue("login_wikipedia1_url"));
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
		
		bot = new MediaWikiAdapterBot(url);
		bot.login(getValue("login_wikipedia1_user_valid"), getValue("login_wikipedia1_pass_valid"));
		assertEquals(true, bot.isLoggedIn());
	}
	/**
	 * Test FAIL login on Wikipedia.
	 * @throws Exception a
	 */
	@Test(expected=ActionException.class)
	public final void loginWikipedia1Fail() throws Exception {

			bot = new MediaWikiAdapterBot(getValue("login_wikipedia1_url"));
			bot.login("Klhjfd", "4sdf");
			assertFalse("Login does not exist", bot.isLoggedIn());

	}
		
	/**
	 * Test login on a Mediawiki. 
	 * @throws Exception a
	 */
	@Test
	public final void loginWikiMW1_09() throws Exception {
		assertTrue("shuld end with .php" , getValue("wikiMW1_09_url").endsWith(".php"));
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		assertEquals(true, bot.isLoggedIn());
	}
	
	/**
	 * Test FAIL login on english Mediawiki. 
	 * @throws Exception a
	 */
	@Test(expected = ActionException.class) 
	public final void loginWikiMW1_09Fail() throws Exception {

			bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
			bot.login("Klhjfd", "4sdf");

	}
	/**
	 * Test login where the wiki is in a subfolder, like www.abc.com/wiki
	 * @throws Exception a
	 */
	@Test(expected = MalformedURLException.class) 
	public final void loginWikiMW1_09UrlformatsFail() throws Exception {
		
		String url = getValue("wikiMW1_09_url");
		int lastSlash = url.lastIndexOf("/");
		url = url.substring(0, lastSlash);
		assertFalse("shuld not end with .php" , url.endsWith(".php"));
		bot = new MediaWikiAdapterBot(url);
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
	}
	

	
	

	
	/**
	 * Test login on a Mediawiki. 
	 * @throws Exception a
	 */
	@Test
	public final void loginWikiMW1_09Urlformats() throws Exception {
		

		String url = getValue("wikiMW1_09_url");
		int lastSlash = url.lastIndexOf("/");
		url = url.substring(0, lastSlash + 1);
		assertFalse("shuld not end with .php" , url.endsWith(".php"));
		bot = new MediaWikiAdapterBot(url);
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		assertEquals(true, bot.isLoggedIn());
	}
	
	
	


}
