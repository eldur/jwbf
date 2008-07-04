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


import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.Siteinfo;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class SiteinfoTest extends LiveTestFather {

	
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
	public final void SiteInfoWikipediaDe() throws Exception {
		
		bot = new MediaWikiBot("http://de.wikipedia.org/w/index.php");
		Siteinfo is = bot.getSiteinfo();
		System.out.println(is);
	}
	
	

	
}

