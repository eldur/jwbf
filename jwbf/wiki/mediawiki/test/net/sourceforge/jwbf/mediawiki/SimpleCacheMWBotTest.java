package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.CacheHandler;
import net.sourceforge.jwbf.bots.util.SimpleCachTest;
import net.sourceforge.jwbf.bots.util.SimpleCache;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * Tests cache.
 * @author Thomas Stock
 */
public class SimpleCacheMWBotTest extends LiveTestFather {

	
	
	private static String label = "CachTest";
	private File f = new File(SimpleCachTest.CACHFOLDER);
	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	
	@Before
	public final void prepare()  throws Exception {
		f.mkdirs();
	}

	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestWithBot() throws Exception {
		
		MediaWikiBot bot;
		bot = new MediaWikiBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		
		CacheHandler c = new SimpleCache(f, 10000);
		bot.setCacheHandler(c);
		Article a = new Article(bot, label);
		String text = getRandom(8);
		a.setText(text);
		a.save();
		assertTrue("should contains the article", c.containsKey(label));
		assertEquals(text, c.get(label).getText());
		
		Article b = new Article(bot, label);
		assertEquals(text, b.getText());
		
	}
	

	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestTimeout() throws Exception {
		
		MediaWikiBot bot;
		bot = new MediaWikiBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		CacheHandler c = new SimpleCache(f, 1000);
		bot.setCacheHandler(c);
		SimpleArticle a = new SimpleArticle(label);
		String text = getRandom(8);
		a.setText(text);
		c.put(a);
		assertTrue("should contains the article", c.containsKey(label));
		assertEquals(text, c.get(label).getText());
		
		SimpleArticle b = c.get(label);
		assertEquals(text, b.getText());
		synchronized (this) {
			wait(1000);
		}
		
		assertFalse("should not contains the article", c.containsKey(label));
		SimpleArticle d = c.get(label);
		assertEquals(text, d.getText());
	}
	
	
	
	@After
	public final void afterTest() {
		
		File [] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
//			fs[i].delete(); // TODO comment in
		}
//		f.deleteOnExit();  // TODO comment in

	}
}
