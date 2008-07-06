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
import net.sourceforge.jwbf.bots.MediaWikiBot;
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
public class AllPagesTest extends LiveTestFather {

	
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
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void allPagesWikipediaDe() throws Exception {
		
		bot = new MediaWikiBot("http://de.wikipedia.org/w/index.php");
		Iterator<String> is = bot.getAllPageTitles(0).iterator();
		int i = 0;

		while (is.hasNext()) {

			i++;
			if (i > 55) {
				break;
			}

		}

		Assert.assertTrue("i is: " + i , i > 50 );
	}
	
//	/**
//	 * Test category read. Test category must have more then 50 members.
//	 * @throws Exception a
//	 */
//	@Test
//	public final void allPagesWikipediaDeMax() throws Exception {
//		
//		bot = new MediaWikiBot("http://de.wikipedia.org/w/index.php");
//		Iterator<String> is = bot.getAllPageTitles(0).iterator();
//		int i = 0;
//		String out = "";
//		int max = 100;
//		boolean found = false;
//		while (is.hasNext()) {
//			out = is.next();
//			
//			if (out.equals("Bienengift") || found) {
//				i++;
//				found = true;
//			}
//			if (i > max) {
//				break;
//			}
//			
//
//		}
//		System.err.println(out);
//		Assert.assertTrue("i is: " + i , i > 50 );
//	}
	
	

	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void allPagesCustomWiki() throws Exception {
		
		bot = new MediaWikiBot(getValue("allPages_customWiki_url"));
		bot.login(getValue("allPages_customWiki_user"), getValue("allPages_customWiki_pass"));
		Iterator<String> is = bot.getAllPageTitles(0).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		Assert.assertTrue("i is: " + i , i > 50);
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void allPagesWikiMW1_11() throws Exception {
		
		bot = new MediaWikiBot(getValue("allPages_WikiMW1_11_url"));
		bot.login(getValue("allPages_WikiMW1_11_user"), getValue("allPages_WikiMW1_11_pass"));
		Iterator<String> is = bot.getAllPageTitles(0).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > 55) {
				break;
			}
		}
		
		Assert.assertTrue("i is: " + i , i > 50);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
	}
	
}

