package net.sourceforge.jwbf.live;

import java.util.Iterator;

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
public class BacklinkTest extends LiveTestFather {

	private MediaWikiBot bot = null;

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
