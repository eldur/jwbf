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


import java.util.Iterator;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
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
	
	protected static final void prepareTestWikis() throws Exception {
		
		MediaWikiBot bot;
		 
		
		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		
		doPreapare(bot);
		

		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		
		doPreapare(bot);
		
		bot = new MediaWikiBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		
		doPreapare(bot);
		
		
	}
	
	protected static final void doPreapare(MediaWikiBot bot)
			throws ActionException, ProcessException {
		try {
			SimpleArticle a = new SimpleArticle();
			bot.writeContent(a);
			for (int i = 0; i < COUNT; i++) {
				a.setLabel("CategoryTest" + i);
					a.setText("abc [[Category:" 
							+ TESTCATNAME
							+ "]]");
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
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
//		prepareTestWikis();
	}
	

	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikipediaDe() throws Exception {
		
		bot = new MediaWikiBot("http://de.wikipedia.org/w/index.php");
		Iterator<String> is = bot.getCategoryMembers(getValue("category_category")).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}

		}
		Assert.assertTrue("i is: " + i , i > 50 );
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void categoryWikiMW1_09() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		Iterator<String> is = bot.getCategoryMembers(TESTCATNAME).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_09.equals(bot.getVersion()));
		Assert.assertTrue("i is: " + i , i > 50);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void categoryWikiMW1_10() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		Iterator<String> is = bot.getCategoryMembers(TESTCATNAME).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_10.equals(bot.getVersion()));
		Assert.assertTrue("i is: " + i , i > 50);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_11() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		Iterator<String> is = bot.getCategoryMembers(TESTCATNAME).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
		Assert.assertTrue("i is: " + i , i > 50);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_12() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		Iterator<String> is = bot.getCategoryMembers(TESTCATNAME).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_12.equals(bot.getVersion()));
		Assert.assertTrue("i is: " + i , i > 50);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_13() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		Iterator<String> is = bot.getCategoryMembers(TESTCATNAME).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
		Assert.assertTrue("i is: " + i , i > 50);
		
	}
	
}
