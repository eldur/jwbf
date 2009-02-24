package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertEquals;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetRevisionTest extends LiveTestFather {

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
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1_09() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		doTest(bot);	
	}
	
	/**
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1_10() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1_11() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1_12() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void getRevisionMW1_13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		doTest(bot);
	}
	
	/**
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void writeArticleWithSpecialLabels() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		
		SimpleArticle sa;
		SimpleArticle saR;
		String testText = getRandom(255);
		String label;
		
		label = "\"";
		sa = new SimpleArticle(testText, label);
		bot.writeContent(sa);
		
		saR = bot.readContent(label);
		assertEquals(testText, saR.getText());
		assertEquals(label, saR.getLabel());

		
		label = "&";
		sa = new SimpleArticle(testText, label);
		bot.writeContent(sa);
		
		saR = bot.readContent(label);
		assertEquals(testText, saR.getText());
		assertEquals(label, saR.getLabel());
	
	}
	
	
	private final void doTest(MediaWikiBot bot) throws Exception {
		String label = getValue("wikiMW1_12_user");
		SimpleArticle sa;
		String testText = getRandom(255);
		sa = new SimpleArticle(testText, label);
		bot.writeContent(sa);
		
		String text = bot.readContent(label).getText();
		assertEquals(testText, text);	
		
	}
	
}
