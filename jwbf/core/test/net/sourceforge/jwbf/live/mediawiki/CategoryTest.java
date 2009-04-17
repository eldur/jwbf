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


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.queries.FullCategoryMembers;
import net.sourceforge.jwbf.actions.mediawiki.queries.GetSimpleCategoryMembers;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mediawiki.CategoryItem;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class CategoryTest extends LiveTestFather {

	
	private MediaWikiAdapterBot bot = null;
	private static final int COUNT = 60;
	private static final String TESTCATNAME = "TestCat";
	
	protected static final void prepareTestWikis() throws Exception {
		
		MediaWikiAdapterBot bot;
		 
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		
		doPreapare(bot);
		

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		
		doPreapare(bot);
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
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
		
		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
		
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.UNKNOWN.equals(bot.getVersion()));
		
		doTest(bot, "Moose");
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void categoryWikiMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_09.equals(bot.getVersion()));
		
		doTest(bot);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void categoryWikiMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_10.equals(bot.getVersion()));
	
		doTest(bot);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
		
		doTest(bot);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_12.equals(bot.getVersion()));
		doTest(bot);
		
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_13() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
		doTest(bot);
		
		
		
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void categoryWikiMW1_14() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_14.equals(bot.getVersion()));
		doTest(bot);
		
		
		
	}
	
	private final void doTest(MediaWikiBot bot) throws ActionException, ProcessException {
		doTest(bot, TESTCATNAME);
	}
	
	private final void doTest(MediaWikiBot bot, String catname) throws ActionException, ProcessException {
		
		GetSimpleCategoryMembers g = new GetSimpleCategoryMembers(bot, catname);
		bot.performAction(g);
		assertTrue("shuld have next", g.hasNext());
		Collection<String> compare1 = new Vector<String>();
		Collection<CategoryItem> compare2 = new Vector<CategoryItem>();
		Iterator<String> is = new GetSimpleCategoryMembers(bot, catname).iterator();
		int i = 0;
		boolean notEnough = true; 
		while(is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				notEnough = false;
				break;
			}
		}
		if (notEnough) {
			doPreapare(bot);
		}
		
		
		is = new GetSimpleCategoryMembers(bot, catname).iterator();
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
		
		Iterator<CategoryItem> cit = new FullCategoryMembers(bot,catname).iterator();
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
	}
	
}
