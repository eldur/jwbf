package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.queries.ImageUsageTitles;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.Article;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageUsageTitlesTest extends LiveTestFather {


	private MediaWikiBot bot = null;
	private int LIMIT = 55;
	
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
//		prepareTestWikis();
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void imageLinkMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_09.equals(bot.getVersion()));
		test(bot);
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void imageLinkMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_10.equals(bot.getVersion()));
		test(bot);
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void imageLinkMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
		test(bot);
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void imageLinkMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_12.equals(bot.getVersion()));
		test(bot);
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void imageLinkMW1_13() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
		test(bot);
		
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void imageLinkMW1_14() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_14.equals(bot.getVersion()));
		test(bot);
		
	}
	private void test(MediaWikiBot bot2) throws Exception {
		ImageUsageTitles il = new ImageUsageTitles(bot, "Image:" +getValue("filename"), MediaWiki.NS_ALL);
		assertTrue("test not documented for version: " + bot.getVersion() , il.getSupportedVersions().contains(bot.getVersion()));
		boolean notFound = true;
		int x = 0;
		for (String string : il) {
			System.out.println(string);
			x++;
			if (x >= LIMIT) {
				notFound = false;
				break;
			}
		}
		if (notFound) {
			prepare(bot2);
		}
		x = 0;
		for (String string : il) {
			System.out.println(string);
			x++;
			if (x >= LIMIT) {
				break;
			}
		}
		
		if (x < LIMIT) {
			fail("limit" + x);
		}
		
	}
	private void prepare(MediaWikiBot bot2) throws Exception {
		
		String name = "";
		for(int i = 0; i < LIMIT; i++) {
			name = "TitleWithImg" + i;
			Article a = new Article(bot2, name);
			a.setText("Hello [[Image:" + getValue("filename") + "]] a image " + getRandom(10) );
			a.save();
		}
		
	}
}
