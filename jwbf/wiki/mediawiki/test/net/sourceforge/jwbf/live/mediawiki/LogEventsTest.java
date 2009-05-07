package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.queries.LogEvents;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.mediawiki.LogItem;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogEventsTest extends LiveTestFather {


	private MediaWikiBot bot = null;
	private static int LIMIT = 55;
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
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void logEventsWikipediaDe() throws Exception {
		
		bot = new MediaWikiAdapterBot("http://de.wikipedia.org/w/index.php");
		doTest(bot, false, LogEvents.DELETE);
	}

	

	
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void logEventsMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		doTest(bot, true, LogEvents.DELETE);
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_09.equals(bot.getVersion()));
	}
	/**
	 * 
	 * @throws Exception a
	 */
	@Test(expected=VersionException.class)
	public final void logEventsMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		doTest(bot, true, LogEvents.DELETE);
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_10.equals(bot.getVersion()));
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void logEventsMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		doTest(bot, true, LogEvents.UPLOAD);
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
	}
	
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void logEventsMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		doTest(bot, true, LogEvents.DELETE);
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_12.equals(bot.getVersion()));
	}
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void logEventsMW1_13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		doTest(bot, true, LogEvents.DELETE);
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
	}
	/**
	 * 
	 * @throws Exception a
	 */
	@Test
	public final void logEventsMW1_14() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		doTest(bot, true, LogEvents.DELETE);
		assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_14.equals(bot.getVersion()));
	}
	
	private void doPrepare(MediaWikiBot bot) throws Exception {
		for (int i = 0; i <= LIMIT; i++) {
			String title = getRandomAlph(6);
			Article a = new Article(bot, title);
			a.setText(getRandom(5));
			a.save();
			assertTrue("content shoul be", a.getText().length() > 0);
			a.delete();
		}
	}
	
	private void doTest(MediaWikiBot bot, boolean isDemo, String type) throws Exception {
		LogEvents le = new LogEvents(bot, type );
		if (bot.getVersion() != Version.DEVELOPMENT)
			assertTrue("test not documented for version: " + bot.getVersion() , le.getSupportedVersions().contains(bot.getVersion()));
		int i = 0;
		boolean notEnough = true;
		for (LogItem logItem : le) {
			i++;
			if (i > LIMIT) {
				notEnough = false;
				break;
			}
		}
		if (notEnough && isDemo) {
			doPrepare(bot);
		}
		
		for (LogItem logItem : le) {
			System.out.print(logItem.getTitle() + " ");
			i++;
			if (i > LIMIT) {
				break;
			}
		}
		assertTrue("should be greater then 50 but is " + i, i > LIMIT);
	}
}
