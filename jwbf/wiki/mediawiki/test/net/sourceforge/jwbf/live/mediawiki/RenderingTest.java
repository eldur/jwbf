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
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.misc.GetRendering;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class RenderingTest extends LiveTestFather {

	
	private MediaWikiBot bot = null;
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		addInitSupporterVersions(GetRendering.class);

	}


	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void getRenderingWikipediaDe() throws Exception {
		
		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
		doTest(bot);
	}

	/**
	 * 
	 * @throws Exception a
	 */
	@Test(expected = VersionException.class)
	public final void getRenderingMW1x09Fail() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		doTest(bot);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
	}
	/**
	 * 
	 * @throws Exception a
	 */
	@Test(expected = VersionException.class)
	public final void getRenderingMW1x10Fail() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		doTest(bot);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test(expected = VersionException.class)
	public final void getRenderingMW1x11Fail() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		doTest(bot);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void getRenderingMW1x12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		doTest(bot);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
	}
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void getRenderingMW1x13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		Assert.assertEquals("Wrong Wiki Version " + bot.getVersion(), bot.getVersion(), Version.MW1_13);
		doTest(bot);
		
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void getRenderingMW1x14() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		doTest(bot);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void getRenderingMW1x15() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_15_url"));
		bot.login(getValue("wikiMW1_15_user"), getValue("wikiMW1_15_pass"));
		doTest(bot);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
	}
	
	private void doTest(MediaWikiBot bot) throws Exception {
		GetRendering r = new GetRendering("bert", bot);
		if (bot.getVersion() != Version.DEVELOPMENT)
			assertTrue("test not documented for version: " + bot.getVersion() , r.getSupportedVersions().contains(bot.getVersion()));
		Assert.assertEquals("<p>bert</p>", r.getHtml());
		
		registerTestedVersion(GetRendering.class, bot.getVersion());
		// TODO more tests
	}
}

