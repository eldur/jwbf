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
package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

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
	private Random random = new Random(System.currentTimeMillis());
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestHelper.prepareLogging();;
	}

	/**
	 * Setup custom wiki.
	 * @throws Exception a
	 */
	@Before
	public void setUp() throws Exception {

		bot = getMediaWikiBot(Version.MW1_09, true);
	}

	/**
	 * Test content modification.
	 * @throws Exception a
	 */
	@Test
	public final void contentModify() throws Exception {
		String title = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(title);
		sa.setText(getRandom(64));
		try {
			bot.writeContent(sa);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		sa = bot.readContent(title).getSimpleArticle();
		//System.out.println("Content is: " + sa.getText());
		String text = "test " + (random.nextInt(1000));
		sa.setText(text);
		bot.writeContent(sa);
		assertEquals(text, bot.readContent(title).getText());

	}

	/**
	 * Test the read of metadata on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyDetails() throws Exception {
		String title = getValue("test_live_article");
		String summary = "clear it";
		SimpleArticle t = new SimpleArticle(title);
		t.setText(getRandom(64));
		t.setEditSummary(summary);
		t.setMinorEdit(true);
		try {
			bot.writeContent(t);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		SimpleArticle sa = bot.readContent(title).getSimpleArticle();
		assertEquals(title, sa.getTitle());
		assertEquals(summary, sa.getEditSummary());
		assertEquals(bot.getUserinfo().getUsername(), sa.getEditor());
		assertEquals(true, sa.isMinorEdit());

	}

	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifySimpleUtf8Get() throws Exception {
		String utf8value = "öäüÖÄÜß";
		String title = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(title);
		sa.setText(utf8value);
		bot.writeContent(sa);

		sa = bot.readContent(title, GetRevision.CONTENT).getSimpleArticle();

		assertEquals(utf8value, sa.getText());
	}

	/**
	 * Test utf-8 read on english Mediawiki.
	 * @throws Exception a
	 */
	@Test
	public final void contentModifyIPAUtf8Get() throws Exception {
		String utf8value = "ɕɕkɕoːɐ̯eːaɕɐɑɒæɑ̃ɕʌbɓʙβcɕçɕɕçɕɔɔɕɕ";

		String title = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(title);
		sa.setText(utf8value);
		bot.writeContent(sa);
		doWait();
		sa = bot.readContent(title).getSimpleArticle();

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

		String title = getValue("test_live_article");
		SimpleArticle sa;
		sa = new SimpleArticle(title);
		sa.setText(utf8value);
		bot.writeContent(sa);
		doWait();
		sa = bot.readContent(title).getSimpleArticle();

		assertEquals(utf8value, sa.getText());
		assertTrue(sa.getEditTimestamp() != null);
	}

	/**
	 * Test getTimestamp.
	 * @throws Exception a
	 */
	@Test
	public final void getTimestamp() throws Exception {

		String label = getValue("test_live_article");
		ArticleMeta sa;


		sa = bot.readContent(label);

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
		doWait(1500);
	}
	private void doWait(int milis) {
		synchronized (this) {

			try {
				wait(milis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
