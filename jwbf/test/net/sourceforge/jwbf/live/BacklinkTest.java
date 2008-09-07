package net.sourceforge.jwbf.live;

import java.util.Iterator;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.queries.GetBacklinkTitles.RedirectFilter;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.actions.mw.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class BacklinkTest extends LiveTestFather {

	private static final String BACKLINKS = "Backlinks";
	private static final int COUNT = 60;
	private MediaWikiBot bot = null;

	
	protected static final void prepareTestWikis() throws Exception {
		
		MediaWikiBot bot;
		 
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		
		doPreapare(bot);
		
		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_11_pass"));
		
		doPreapare(bot);
		
		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		
		doPreapare(bot);
		

		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		
		doPreapare(bot);
		
		bot = new MediaWikiBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		
		doPreapare(bot);
		
		
	}
	
	protected static final void doPreapare(MediaWikiBot bot) throws ActionException, ProcessException {
		SimpleArticle a = new SimpleArticle("test", "0");
		a = new SimpleArticle("text", BACKLINKS);
		bot.writeContent(a);
		for (int i = 0; i < COUNT; i++) {
			a.setLabel("Back" + i);
			if (i % 2 == 0) {
			a.setText("#redirect [[" + BACKLINKS + "]]");
			} else {
				a.setText("[[" + BACKLINKS + "]]");
			}
			bot.writeContent(a);
		}
	}
	/**
	 * Setup log4j.
	 * 
	 * @throws Exception
	 *             a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
//		prepareTestWikis();
	}

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksWikipediaDe() throws Exception {

		bot = new MediaWikiBot("http://de.wikipedia.org/w/index.php");
		Iterator<String> is = bot.getBacklinkTitles(
				getValue("backlinks_article")).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > getIntValue("backlinks_article_count") + 1) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ getIntValue("backlinks_article_count"),
				i > getIntValue("backlinks_article_count"));
	}

	
	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_09() throws Exception {

		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		
		Iterator<String> is = bot.getBacklinkTitles(
				BACKLINKS).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT ) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ COUNT,
				i > COUNT -1);
	}
	@Test(expected=VersionException.class)
	public final void backlinksMW1_09_redirectVar() throws Exception {

		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		
		Iterator<String> is = bot.getBacklinkTitles(
				BACKLINKS, RedirectFilter.nonredirects).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT ) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ COUNT,
				i > COUNT -1);
	}

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_10() throws Exception {

		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"),
				getValue("wikiMW1_10_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_10);
		
		Iterator<String> is = bot.getBacklinkTitles(
				BACKLINKS).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT ) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ COUNT,
				i > COUNT -1);
	}
	

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_11() throws Exception {

		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"),
				getValue("wikiMW1_11_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_11);
		
		Iterator<String> is = bot.getBacklinkTitles(
				BACKLINKS).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT ) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ COUNT,
				i > COUNT - 1);
	}
	

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_12() throws Exception {

		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"),
				getValue("wikiMW1_12_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_12);
		
		Iterator<String> is = bot.getBacklinkTitles(
				BACKLINKS).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT ) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ COUNT,
				i > COUNT - 1);
	}
	

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_13() throws Exception {

		bot = new MediaWikiBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"),
				getValue("wikiMW1_13_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_13);
		
		Iterator<String> is = bot.getBacklinkTitles(
				BACKLINKS).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT ) {
				break;
			}
		}

		Assert.assertTrue("Fail: " + i + " < "
				+ COUNT,
				i > COUNT -1);
	}
	
	/*
	 * 
DEBUG [main] net.sourceforge.jwbf.actions.HttpActionClient: http://localhost/mediawiki-1.9.6/index.php?title=Back10&redirect=no || POST: HTTP/1.1 200 OK
DEBUG [main] net.sourceforge.jwbf.actions.HttpActionClient: path is: /mediawiki-1.9.6/index.php
DEBUG [main] net.sourceforge.jwbf.actions.HttpActionClient: /mediawiki-1.9.6/index.php?title=Back11&action=edit&dontcountme=s
DEBUG [main] net.sourceforge.jwbf.actions.HttpActionClient: GET: HTTP/1.1 200 OK
	 */
	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksCustomWiki() throws Exception {

		bot = new MediaWikiBot(getValue("backlinks_customWiki_url"));
		bot.login(getValue("backlinks_customWiki_user"),
				getValue("backlinks_customWiki_pass"));
		Iterator<String> is = bot.getBacklinkTitles(
				getValue("backlinks_customWiki_article")).iterator();
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > getIntValue("backlinks_customWiki_article_count") + 1) {
				break;
			}
		}
		Assert.assertTrue("Fail: " + i + " < "
				+ getIntValue("backlinks_customWiki_article_count"),
				i > getIntValue("backlinks_customWiki_article_count"));
	}
}
