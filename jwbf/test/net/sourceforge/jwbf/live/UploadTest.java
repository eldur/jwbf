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

import java.io.File;
import java.io.FileInputStream;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Assert;
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
		bot.uploadFile(getValue("validFile"));
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
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		
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
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void uploadMW1_09() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));


		
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void uploadMW1_10() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"),
				getValue("wikiMW1_10_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_10);
		
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));

	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void uploadMW1_11() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"),
				getValue("wikiMW1_11_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_11);
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		bot.uploadFile(sf);
		
		String url = bot.getImageInfo(sf.getFilename());
		File file = new File(getValue("validFile"));
		
		assertFile(url, file);
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void uploadMW1_12() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"),
				getValue("wikiMW1_12_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_12);
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		String url = bot.getImageInfo(sf.getFilename());
		File file = new File(getValue("validFile"));
		assertFile(url, file);
	}
	@After
	public final void cleanUp() {
		
	}
	
	protected final void assertFile(String url, File file) throws Exception {
		byte [] s = bot.getBytes(url);
		
		
		byte buff1[]=new byte[512];
		System.out.println(file.length());
		FileInputStream fis = new FileInputStream(file);
		int read = fis.read(buff1);
		for(int i =0; i<read; i++) {
			Assert.assertEquals(buff1[i], s[i]);
			if (buff1[i] != s[i]) {
				System.err.println(buff1[i] + " " + s[i]);
			}
//		 System.out.print( + "  ");
//		 System.out.println();
		}
		fis.close();
	}
}
