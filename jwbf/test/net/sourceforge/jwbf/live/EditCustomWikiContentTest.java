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


import static org.junit.Assert.assertEquals;

import java.util.Random;

import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class EditCustomWikiContentTest extends LiveTestFather {

	private MediaWikiBot bot;
	
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}

	/**
	 * Setup custom wiki.
	 * @throws Exception a
	 */
	@Before
	public void setUp() throws Exception {
		bot = new MediaWikiBot(getValue("editCustomWikiContent_url"));
		bot.login(getValue("editCustomWikiContent_user"), getValue("editCustomWikiContent_pass"));
	}

	
	/**
	 * Test content modification.
	 * @throws Exception a
	 */
	@Test
	public final void contentModify() throws Exception {
		String label = getValue("editCustomWikiContent_article");
		SimpleArticle sa;
		sa = new SimpleArticle("", label);
		bot.writeContent(sa);
		sa = new SimpleArticle(bot.readContent(label));
		//System.out.println("Content is: " + sa.getText());
		int x = (Math.abs(new Random(System.currentTimeMillis()).nextInt()));
		String text = "hello " + x;
		sa.setText(text);
		bot.writeContent(sa);
		assertEquals(text, bot.readContent(label).getText());
		
	
	}
	
	/**
	 * Test the read of metadata on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyDetails() throws Exception {
		String label = getValue("editCustomWikiContent_article");
		String summary = "clear it";
		SimpleArticle t = new SimpleArticle("", label);
		t.setEditSummary(summary);
		t.setMinorEdit(true);
		bot.writeContent(t);
		SimpleArticle sa = new SimpleArticle(bot.readContent(label));
		assertEquals(label, sa.getLabel());
//		System.out.println(sa.getEditSummary());
		assertEquals(summary, sa.getEditSummary());
		assertEquals(getValue("editCustomWikiContent_user"), sa.getEditor());
//		assertEquals(true, sa.isMinorEdit()); // TODO 
		
		
	
	}
	
	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyUtf8Get() throws Exception {
		String utf8value = "öäüÖÄÜß Лин 瓦茲القواميس والمراجع";
		utf8value = "öäüÖÄÜß";
		String label = getValue("editCustomWikiContent_article");
		SimpleArticle sa;
		sa = new SimpleArticle(utf8value, label);
		bot.writeContent(sa);
		
		
		
		sa = new SimpleArticle(bot.readContent(label));
		
//		System.out.println("Content is: "+ sa.getText());
		
		assertEquals(sa.getText(), utf8value);
	}
	
}
