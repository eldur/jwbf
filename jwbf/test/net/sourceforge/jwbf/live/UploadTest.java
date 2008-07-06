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

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class UploadTest extends LiveTestFather {

	

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
	 * Test upload. .txt must be a valid extension
	 * @throws Exception a
	 */
	@Test
	public final void uploadCustomWiki() throws Exception {
		
		bot = new MediaWikiBot(getValue("upload_customWiki_url"));
		bot.login(getValue("upload_customWiki_user"),
				getValue("upload_customWiki_pass"));
		bot.uploadFile("buildAdds/CHANGELOG.txt");
	}
	/**
	 * Test upload. .txt must be a valid extension
	 * @throws Exception a
	 */
	@Test
	public final void uploadCustomWikiComplex() throws Exception {
		
		bot = new MediaWikiBot(getValue("upload_customWiki_url"));
		bot.login(getValue("upload_customWiki_user"),
				getValue("upload_customWiki_pass"));
		SimpleFile sf = new SimpleFile("Changelog.txt", "buildAdds/CHANGELOG.txt");
		
		bot.uploadFile(sf);
	}
	/**
	 * Test upload.
	 * @throws Exception a
	 */
	@Test(expected=ProcessException.class)
	public final void uploadCustomWikiFail() throws Exception {
		
		bot = new MediaWikiBot(getValue("upload_customWiki_url"));
		bot.login(getValue("upload_customWiki_user"),
				getValue("upload_customWiki_pass"));
		bot.uploadFile("README");
	}
}
