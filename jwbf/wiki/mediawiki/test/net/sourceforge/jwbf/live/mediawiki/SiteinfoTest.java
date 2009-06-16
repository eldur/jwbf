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
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.meta.GetVersion;
import net.sourceforge.jwbf.actions.mediawiki.meta.Siteinfo;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.Article;

import org.apache.log4j.PropertyConfigurator;
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
		addInitSupporterVersions(Siteinfo.class);
	}
	

	/**
	 * Test get siteinfo on Wikipedia DE.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoWikipediaDe() throws Exception {
		
		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
		doTest(bot, Version.DEVELOPMENT);
		
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));

		doTest(bot, Version.MW1_09);
		
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		doTest(bot, Version.MW1_10);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));

		doTest(bot, Version.MW1_11);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));

		doTest(bot, Version.MW1_12);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x13() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		doTest(bot, Version.MW1_13);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x14() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		doTest(bot, Version.MW1_14);
	}
	
	/**
	 * Test get siteinfo on a MW.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoMW1x15() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_15_url"));
		doTest(bot, Version.MW1_15);
	}
	
	/**
	 * Test last Version.
	 * @throws Exception a
	 */
	@Test
	public final void siteInfoLastVersion() throws Exception {
		Version v = Version.MW1_15;
		String versionInfoTemplate = "Template:MW stable release number";
		MediaWikiBot bot = new MediaWikiBot("http://www.mediawiki.org/w/api.php");
		
		Article a = new Article(bot, versionInfoTemplate);
		
		
		assertTrue(a.getText() + " should contains " + v.getNumber() , a.getText().contains(v.getNumber()));
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
		if (v.greaterEqThen(Version.MW1_11)) {
			System.out.println(si.getInterwikis());
			assertTrue("shuld have interwikis", si.getInterwikis().size() > 5);
		
			System.out.println(si.getNamespaces());
			assertTrue("shuld have namespaces", si.getNamespaces().size() > 15);
			registerTestedVersion(Siteinfo.class, v);
		}
		registerTestedVersion(GetVersion.class, v);
	}
	
	

	
}

