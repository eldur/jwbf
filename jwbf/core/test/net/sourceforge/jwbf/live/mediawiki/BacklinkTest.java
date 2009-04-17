package net.sourceforge.jwbf.live.mediawiki;

import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.queries.BacklinkTitles;
import net.sourceforge.jwbf.actions.mediawiki.queries.BacklinkTitles.RedirectFilter;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

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
	private MediaWikiAdapterBot bot = null;

	
		
	
	
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

		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
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

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		
		doTest(bot);
	}
	@Test(expected=NullPointerException.class)
	public final void backlinksMW1_09_redirectVar() throws Exception {

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_09);
		doTest(bot, RedirectFilter.redirects);
	}

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_10() throws Exception {

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"),
				getValue("wikiMW1_10_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_10);
		
		doTest(bot);
	}
	

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_11() throws Exception {

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"),
				getValue("wikiMW1_11_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_11);
		
		doTest(bot);
	}
	

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_12() throws Exception {

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"),
				getValue("wikiMW1_12_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_12);
		
		doTest(bot);
	}
	

	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_13() throws Exception {

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"),
				getValue("wikiMW1_13_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_13);
		doTest(bot);
		
	}
	
	/**
	 * Test backlinks.
	 * 
	 * @throws Exception
	 *             a
	 */
	@Test
	public final void backlinksMW1_14() throws Exception {

		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"),
				getValue("wikiMW1_14_pass"));
		Assert.assertEquals(bot.getVersion(), Version.MW1_14);
		doTest(bot);
		
	}

	private final void doTest(MediaWikiBot bot) throws Exception {
		doTest(bot, RedirectFilter.all);
	}
	
	private final void doTest(MediaWikiBot bot, RedirectFilter rf) throws Exception {

		BacklinkTitles gbt = new BacklinkTitles(bot, BACKLINKS, rf, MediaWiki.NS_MAIN , MediaWiki.NS_CATEGORY);

		Vector<String> vx = new Vector<String>();
		Iterator<String> is = gbt.iterator();
		boolean notEnougth = true;
		int i = 0;
		while(is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT) {
				notEnougth = false;
				break;
			}
		}
		if(notEnougth) {
			doPreapare(bot);
		}
		is = gbt.iterator();
		vx.add(is.next());
		vx.add(is.next());
		vx.add(is.next());
		is = gbt.iterator();
		i = 0;
		while (is.hasNext()) {
			String buff = is.next();
			vx.remove(buff);
			i++;
			if (i > COUNT) {
				break;
			}
		}
		Assert.assertTrue("Iterator should contain: " + vx ,vx.isEmpty());
		Assert.assertTrue("Fail: " + i + " < " + COUNT, i > COUNT - 1);
	}
}
