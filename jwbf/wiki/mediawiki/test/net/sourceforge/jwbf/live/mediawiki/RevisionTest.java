package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertEquals;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.editing.GetApiToken;
import net.sourceforge.jwbf.actions.mediawiki.editing.GetRevision;
import net.sourceforge.jwbf.actions.mediawiki.editing.PostModifyContent;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.ArticleMeta;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class RevisionTest extends LiveTestFather {

	private MediaWikiAdapterBot bot;
	
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		addInitSupporterVersions(GetRevision.class);
		addInitSupporterVersions(PostModifyContent.class);
		addInitSupporterVersions(GetApiToken.class);
		
	}

	/**
	 * Test write and read.
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x09() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		doTest(bot);	
	}
	
	/**
	 * Test write and read. 
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x10() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read.
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x11() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read.
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x12() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read.
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read.
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x14() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read.
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1x15() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_15_url"));
		bot.login(getValue("wikiMW1_15_user"), getValue("wikiMW1_15_pass"));
		doTest(bot);
	}
	
	
	
	private void doTest(MediaWikiBot bot) throws Exception {
		
		String label = getValue("wikiMW1_12_user");
		String user = bot.getUserinfo().getUsername();
		SimpleArticle sa;
		// test with content length > 0
		String testText = getRandom(255);
		sa = new SimpleArticle(testText, label);
		bot.writeContent(sa);
//		
		
		ArticleMeta a = bot.readContent(label);
		assertEquals(testText, a.getText());	
		assertEquals(user, a.getEditor());	
		// test with content length <= 0
		testText = "";
		label = "767676885340589358058903589035";
		a = bot.readContent(label);
		
		assertEquals(testText, a.getText());
		registerTestedVersion(GetRevision.class, bot.getVersion());
		registerTestedVersion(PostModifyContent.class, bot.getVersion());
		if (bot.getVersion().greaterEqThen(Version.MW1_12)) {
			registerTestedVersion(GetApiToken.class, bot.getVersion());
		}
		
	}
	
}
