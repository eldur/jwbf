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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mw.editing.FileUpload;
import net.sourceforge.jwbf.actions.mw.queries.GetImageInfo;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleFile;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UploadAndImageInfoTest extends LiveTestFather {

	

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
	 * Test upload.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void uploadMW1_09Fail() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		bot.uploadFile("README");
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void uploadMW1_10Fail() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"),
				getValue("wikiMW1_10_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_10);
		
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		URL url = new URL(bot.getImageInfo(sf.getFilename()));
		assertFile(url, sf.getFile());

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
		Assert.assertTrue(new File(getValue("validFile")).canRead());
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		FileUpload up = new FileUpload(sf, bot);
		bot.performAction(up);
		
		
		GetImageInfo gi = new GetImageInfo(sf.getLabel(), bot.getVersion(), bot.getHostUrl());
		bot.performAction(gi);
		URL url = new URL(gi.getUrlAsString());
		System.out.println(url);
		Assert.assertTrue("file not found " + url ,url.toExternalForm().length() - bot.getHostUrl().length() > 2);
		assertFile(url, sf.getFile());
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
		Assert.assertTrue(new File(getValue("validFile")).canRead());
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		FileUpload up = new FileUpload(sf, bot);
		bot.performAction(up);
		URL url = new URL(bot.getImageInfo(sf.getLabel()));
		File file = new File(getValue("validFile"));
		Assert.assertTrue("file not found " + url, url.toExternalForm().length() - bot.getHostUrl().length() > 2);
		assertFile(url, file);
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void uploadMW1_13() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"),
				getValue("wikiMW1_13_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_13);
		Assert.assertTrue(new File(getValue("validFile")).canRead());
		SimpleFile sf = new SimpleFile("Test.gif", getValue("validFile"));
		FileUpload up = new FileUpload(sf, bot);
		bot.performAction(up);
		URL url = new URL(bot.getImageInfo(sf.getLabel()));
		Assert.assertTrue("file not found " + url , url.toExternalForm().length() - bot.getHostUrl().length() > 2);
		File file = new File(getValue("validFile"));
		assertFile(url, file);
	}
	
	protected final void assertFile(URL url, File file) throws Exception {
		File temp = new File("temp.file");
		download(url.toExternalForm(), temp);
		Assert.assertTrue("files are not ident", filesAreIdentical(temp, file));
//		byte [] s = bot.getBytes(url);
//		
//		
//		byte buff1[]=new byte[1024];
//		System.out.println(file.length());
//		FileInputStream fis = new FileInputStream(file);
//		int read = fis.read(buff1);
//		for(int i =0; i<read; i++) {
//			Assert.assertEquals(buff1[i], s[i]);
//			if (buff1[i] != s[i]) {
//				System.err.println(buff1[i] + " " + s[i]);
//			}
////		 System.out.print( + "  ");
////		 System.out.println();
//		}
//		fis.close();
		temp.delete();
	}

	protected static final void download(String address, File localFileName) {
		OutputStream out = null;
		URLConnection conn = null;
		InputStream  in = null;
		try {
			URL url = new URL(address);
			out = new BufferedOutputStream(
				new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
			System.out.println(localFileName + "\t" + numWritten);
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
			}
		}
	}

	
	protected static final boolean filesAreIdentical(File left, File right)
			throws IOException {
		assert left != null;
		assert right != null;
		assert left.exists();
		assert right.exists();

		if (left.length() != right.length())
			return false;

		FileInputStream lin = new FileInputStream(left);
		FileInputStream rin = new FileInputStream(right);
		try {
			byte[] lbuffer = new byte[4096];
			byte[] rbuffer = new byte[lbuffer.length];
			for (int lcount = 0; (lcount = lin.read(lbuffer)) > 0;) {
				int bytesRead = 0;
				for (int rcount = 0; (rcount = rin.read(rbuffer, bytesRead,
						lcount - bytesRead)) > 0;) {
					bytesRead += rcount;
				}
				for (int byteIndex = 0; byteIndex < lcount; byteIndex++) {
					if (lbuffer[byteIndex] != rbuffer[byteIndex])
						return false;
				}
			}
		} finally {
			lin.close();
			rin.close();
		}
		return true;
	}
}
