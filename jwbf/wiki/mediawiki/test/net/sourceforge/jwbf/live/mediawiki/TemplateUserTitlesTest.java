package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Vector;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.queries.TemplateUserTitles;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;
import net.sourceforge.jwbf.contentRep.Article;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class TemplateUserTitlesTest extends LiveTestFather {

	
	private MediaWikiBot bot = null;
	private static final String TESTPATTERNNAME = "Template:ATesT";
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		addInitSupporterVersions(TemplateUserTitles.class);
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void templateUserWikiMW1_09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		doRegularTest(bot);

		assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void templateUserWikiMW1_10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		doRegularTest(bot);

		assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void templateUserWikiMW1_11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		doRegularTest(bot);

		assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void templateUserWikiMW1_12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		doRegularTest(bot);

		assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void templateUserWikiMW1_13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		doRegularTest(bot);

		assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void templateUserWikiMW1_14() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		doRegularTest(bot);

		assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
	}

	private void doRegularTest(MediaWikiBot bot) throws JwbfException {
		TemplateUserTitles a = new TemplateUserTitles(bot, TESTPATTERNNAME, MediaWiki.NS_ALL);
		
		int i = 0;
		Collection<String> titles = new Vector<String>();
		
		for (int j = 0; j < 55; j++)
		titles.add("Patx" + j);
		
		for (String pageTitle : a) {
			i++;
		}
		if (i < 50) {
			prepare(bot, titles);
		}
		
		for (String pageTitle : a) {
			titles.remove(pageTitle);
			System.out.println(titles);
			i++;
		}
		if (i < 50) {
			fail("to less " + i);
		}
		assertTrue("title collection should be empty", titles.isEmpty());
		
		
		
		Article template = new Article(bot, TESTPATTERNNAME);
		assertEquals(TESTPATTERNNAME + " content ", "a test", template.getText());
		registerTestedVersion(TemplateUserTitles.class, bot.getVersion());
	}

	private void prepare(MediaWikiBot bot, Collection<String> titles) throws JwbfException {
		Article template = new Article(bot, TESTPATTERNNAME);
		template.setText("a test");
			template.save();
		
		for (String title : titles) {
			Article a = new Article(bot, title);
			a.setText(getRandom(1) + " {{" + TESTPATTERNNAME + "}}");
			a.save();
		}
		
	}
}
