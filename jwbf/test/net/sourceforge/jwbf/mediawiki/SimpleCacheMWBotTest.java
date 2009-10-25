package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.bots.util.CacheHandler;
import net.sourceforge.jwbf.core.bots.util.SimpleCachTest;
import net.sourceforge.jwbf.core.bots.util.SimpleCache;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
/**
 * Tests cache.
 * @author Thomas Stock
 */
@Ignore
public class SimpleCacheMWBotTest extends LiveTestFather {

	
	
	private static String label = "CachTest";
	private File f = new File(SimpleCachTest.CACHFOLDER);
	
	@BeforeClass
	public static void setUp() throws Exception {
		TestHelper.prepareLogging();
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
		bot = getMediaWikiBot(Version.getLast(), true);
		
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
	public final void cacheTestSpeedBonus() throws Exception {
		
		MediaWikiBot bot;
		bot = getMediaWikiBot(Version.getLast(), true);
		
		CacheHandler c = new SimpleCache(f, 1000);
		bot.setCacheHandler(c);
		long tx = System.currentTimeMillis();
		Article a = new Article(bot, "CacheMax");
		
		if (a.getText().length() < 1000) {
			for (int i = 0; i < 1000; i++) {
				a.addTextnl(getRandom(128));
			}
			a.save();
			fail();
		}
		
		long first = System.currentTimeMillis() - tx;
		System.out.println(first);
		bot = new MediaWikiBot(getValue("wikiMW1_14_url"));
		c = new SimpleCache(f, 10000);
		bot.setCacheHandler(c);
		tx = System.currentTimeMillis();
		a = new Article(bot, "Line of succession to the British throne");
		a.getText();
		long last = System.currentTimeMillis() - tx;
		System.out.println(last + " vs. " + first);
		assertTrue("loading from cach should be faster (first read: " 
				+ first + "ms / cache read: " + last + "ms )", first > last);
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheModifcationTest() throws Exception {
		
		MediaWikiBot bot;
		bot = getMediaWikiBot(Version.getLast(), true);
		CacheHandler c = new SimpleCache(f, 10000);
		bot.setCacheHandler(c);
		Article a = new Article(bot, label);
		String text = getRandom(32);
		a.setText(text);
		a.save();
		assertTrue("shuld have a revId", a.getRevisionId().length() > 0);
		
		Article b = new Article(bot, label);

		assertEquals(text, b.getText());
		text = getRandom(32);
		b.setText(text);
		b.save();
		assertEquals(text, a.getText());
//		MediaWikiBot bot2;
//		bot2 = getMediaWikiBot(Version.MW1_15, true);
//		synchronized (this) {
//			wait(1000);
//		}
		


	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestTimeout() throws Exception {
		
		MediaWikiBot bot;
		bot = getMediaWikiBot(Version.getLast(), true);
		CacheHandler c = new SimpleCache(f, 1000);
		bot.setCacheHandler(c);
		SimpleArticle a = new SimpleArticle(label);
		String text = getRandom(8);
		a.setText(text);
		assertTrue("should contains the article", c.containsKey(label));
		assertEquals(text, c.get(label).getText());
		
		SimpleArticle b = c.get(label);
		assertEquals(text, b.getText());
		synchronized (this) {
			wait(1000);
		}
		
		assertFalse("should not contains the article", c.containsKey(label));
		SimpleArticle d = c.get(label);
		assertNotSame(text, d.getText());
	}
	
	
	
	@After
	public final void afterTest() {
		
		File [] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			fs[i].delete(); // TODO comment in
		}
		f.deleteOnExit();  // TODO comment in

	}
}
