package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.CacheHandler;
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
public class SimpleCacheTest extends LiveTestFather {

	
	private MediaWikiBot bot;
	private static String label = "CachTest";
	private File f = new File("data");
	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	
	@Before
	public final void prepare()  throws Exception {
		bot = new MediaWikiBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		
		f.mkdir();
	}

	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestWithBot() throws Exception {
		
		
		
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
	
	@Test
	public synchronized void basic1() throws Exception {

		String title = getRandom(8);
		CacheHandler db = new SimpleCache(f, 10000);
		SimpleArticle sai = new SimpleArticle();
		sai.setLabel(title);
		sai.setText(getRandom(8));
		sai.setEditTimestamp(new Date());
		db.put(sai);
		SimpleArticle sa = new SimpleArticle();
		db = new SimpleCache(f, 10000);
		assertTrue("should contains", db.containsKey(title));
		assertTrue("should have a", db.get(title).getLabel().length() > 1);
		SimpleArticle sa2 = new SimpleArticle();
		sa2.setLabel(title);
		sa2.setText(getRandom(8));
		db.put(sa);
		sa.setLabel(title);
		db = new SimpleCache(f, 10000);
		assertTrue("should contains", db.containsKey(title));
		assertTrue("should have a", db.get(title).getLabel().length() > 1);

	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestTimeout() throws Exception {
		

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
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestAttributes() throws Exception {

		CacheHandler cache = new SimpleCache(f, 1000);
	
		SimpleArticle a = new SimpleArticle(label);
		a.setText(getRandom(16));
		a.setEditSummary(getRandom(16));
		a.setMinorEdit(true);
		a.setEditTimestamp(new Date());
		a.setEditor("Editor");

		
		cache.put(a);
		assertTrue("should contains the article", cache.containsKey(label));
		
		SimpleArticle b = cache.get(label);
		assertEquals(a.getText(), b.getText());
		assertEquals(a.getEditSummary(), b.getEditSummary());
		assertEquals(a.isMinorEdit(), b.isMinorEdit());
		assertEquals(a.getEditTimestamp(), b.getEditTimestamp());
		assertEquals(a.getEditor(), b.getEditor());
		
	}
	
	@After
	public final void afterTest() {
		
		File [] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
//			fs[i].delete(); // TODO comment in
		}
//		f.deleteOnExit();  // TODO comment in
		Article b = new Article(bot, label);
		try {
			b.delete();
		} catch (ActionException e) {
			e.printStackTrace();
		} catch (ProcessException e) {
			e.printStackTrace();
		}
	}
}
