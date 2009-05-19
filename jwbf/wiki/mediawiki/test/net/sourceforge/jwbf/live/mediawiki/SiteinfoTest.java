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
import net.sourceforge.jwbf.actions.mediawiki.meta.GetVersion;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mediawiki.Siteinfo;

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
		addInitSupporterVersions(GetVersion.class);
	}
	

	/**
	 * Test get siteinfo on Wikipedia DE.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoWikipediaDe() throws Exception {
		
		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
		Siteinfo is = bot.getSiteinfo();
		Assert.assertEquals(Version.DEVELOPMENT, bot.getVersion());
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));

		doTest(bot, Version.MW1_09);
		
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		doTest(bot, Version.MW1_10);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));

		doTest(bot, Version.MW1_11);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));

		doTest(bot, Version.MW1_12);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_13() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		doTest(bot, Version.MW1_13);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1_14() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		doTest(bot, Version.MW1_14);
	}

	private void doTest(MediaWikiBot bot, Version v) throws Exception {
		Assert.assertEquals(v, bot.getVersion());
		registerTestedVersion(GetVersion.class, v);
	}
	
	

	
}

