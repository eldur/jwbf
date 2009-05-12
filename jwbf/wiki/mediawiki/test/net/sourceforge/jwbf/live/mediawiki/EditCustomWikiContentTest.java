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

import java.util.Random;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.ArticleMeta;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

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

	private MediaWikiAdapterBot bot;

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
//		bot = new MediaWikiAdapterBot(getValue("test_live_url"));
//		bot.login(getValue("test_live_user"),
//				getValue("test_live_pass"));
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
	}

	/**
	 * Test content modification.
	 * @throws Exception a
	 */
	@Test
	public final void contentModify() throws Exception {
		String label = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle("", label);
		try {
			bot.writeContent(sa);
		} catch (RuntimeException e) {
			;
		}
		sa = bot.readContent(label);
		//System.out.println("Content is: " + sa.getText());
		int x = (Math.abs(new Random(System.currentTimeMillis()).nextInt()));
		String text = "test " + x;
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
		String label = getValue("test_live_article");
		String summary = "clear it";
		SimpleArticle t = new SimpleArticle("", label);
		t.setEditSummary(summary);
		t.setMinorEdit(true);
		try {
			bot.writeContent(t);
		} catch (RuntimeException e) {
			;
		}
		SimpleArticle sa = bot.readContent(label);
		assertEquals(label, sa.getLabel());
		//		System.out.println(sa.getEditSummary());
		assertEquals(summary, sa.getEditSummary());
		assertEquals(bot.getUserinfo().getUsername(), sa.getEditor());
		//		assertEquals(true, sa.isMinorEdit()); // TODO 

	}

	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifySimpleUtf8Get() throws Exception {
		String utf8value = "öäüÖÄÜß";
		String label = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(utf8value, label);
		bot.writeContent(sa);

		sa = bot.readContent(label, SimpleArticle.CONTENT);

		assertEquals(utf8value, sa.getText());
	}

	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyIPAUtf8Get() throws Exception {
		String utf8value = "ɕɕkɕoːɐ̯eːaɕɐɑɒæɑ̃ɕʌbɓʙβcɕçɕɕçɕɔɔɕɕ";

		String label = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(utf8value, label);
		bot.writeContent(sa);
		doWait();
		sa = bot.readContent(label);

		assertEquals(utf8value, sa.getText());
	}

	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyComplexUtf8Get() throws Exception {
		String utf8value = "öä 品 üÖÄÜß り新しく作成したりできます Л"
				+ "ин 瓦茲القواميس والمراجع";

		String label = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(utf8value, label);
		bot.writeContent(sa);
		doWait();
		sa = bot.readContent(label);

		assertEquals(utf8value, sa.getText());
		assertTrue(sa.getEditTimestamp() != null);
	}
	
	/**
	 * Test getTimestamp
	 * @throws Exception a
	 */
	@Test
	public final void getTimestamp() throws Exception {

		String label = getValue("test_live_article");
		ArticleMeta sa;


		sa = bot.readContent(label, SimpleArticle.TIMESTAMP | SimpleArticle.CONTENT);

		assertTrue(sa.getEditTimestamp().getTime() > 1000);
	}

	
	
	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyOnOtherWiki() throws Exception {
		MediaWikiBot bot = new MediaWikiBot(getValue("demoWiki_url"));
		bot.useEditApi(false);
		bot.login(getValue("demoWiki_user"), getValue("demoWiki_pass"));
		assertTrue("Version is: " + bot.getVersion() , bot.getVersion() == Version.MW1_13);

		
		Article a = new Article(bot, getValue("demoWiki_article"));
//		System.out.println(a.getText());
		a.addText(getRandom(5) + "\nK");
		a.save();
		
		Article b = new Article(bot, getValue("demoWiki_article"));

		assertEquals(a.getText(), b.getText());
	}
	private void doWait() {
//		doWait(1500);
	}
	private void doWait(int milis) {
		synchronized (this) {

			try {
				wait(milis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
