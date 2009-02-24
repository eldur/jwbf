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


import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class SiteinfoTest extends LiveTestFather {

	
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
	 * Test get siteinfo on Wikipedia DE.
	 * @throws Exception a
	 */
	//@Test
	public final void siteInfoWikipediaDe() throws Exception {
		
		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
		Siteinfo is = bot.getSiteinfo();
		System.out.println(is);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));

		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));

		Assert.assertEquals(bot.getVersion(), Version.MW1_10);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));

		Assert.assertEquals(bot.getVersion(), Version.MW1_11);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));

		Assert.assertEquals(bot.getVersion(), Version.MW1_12);
//		Assert.assertTrue("WriteAPI is disabled", bot.getSiteinfo().isWriteAPI());
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_13() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_13);
//		Assert.assertTrue("WriteAPI is disabled", bot.getSiteinfo().isWriteAPI());
	}

	
	

	
}

