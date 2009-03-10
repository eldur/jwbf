package net.sourceforge.jwbf.live.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
import net.sourceforge.jwbf.bots.WikiBot;
import net.sourceforge.jwbf.contentRep.Article;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
public class ArticleTest extends LiveTestFather {
	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void play() throws Exception {
		
		WikiBot bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));

		for (int i = 0; i <= 5; i++) {
			String title = getRandomAlph(6);
			Article a = new Article(bot, title);
			a.setText(getRandom(42));
			a.save();
			
			Article b = new Article(bot, title);
			assertEquals(a.getLabel(), b.getLabel());
			assertEquals(a.getText(), b.getText());
			a.delete();
//			assertTrue("should be empty", b.getText().length() < 1);
			
		}
		// TODO RESUME here
		
		
		
		
	}

}
