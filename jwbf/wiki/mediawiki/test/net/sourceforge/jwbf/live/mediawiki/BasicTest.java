package net.sourceforge.jwbf.live.mediawiki;

import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.live.ArticleTest;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
public class BasicTest extends ArticleTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		WikiBot bot;
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		bots.add(bot);
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		bots.add(bot);
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		bots.add(bot);
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		bots.add(bot);
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		bots.add(bot);
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		bots.add(bot);
	}
	
	@Before
	public void doNothing() {
		
	}
	
	

}
