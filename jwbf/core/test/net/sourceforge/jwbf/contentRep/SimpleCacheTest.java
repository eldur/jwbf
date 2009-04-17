package net.sourceforge.jwbf.contentRep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.CacheHandler;
import net.sourceforge.jwbf.bots.util.SimpleCache;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class SimpleCacheTest extends LiveTestFather {

	
	private MediaWikiBot bot;
	private static String LABEL = "CachTest";
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	
	@Before
	public final void prepare()  throws Exception {
		bot = new MediaWikiBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestSimple() throws Exception {
		

		CacheHandler c = new SimpleCache();
		bot.setCacheHandler(c);
		Article a = new Article(bot, LABEL);
		String text = getRandom(8);
		a.setText(text);
		a.save();
		assertTrue("should contains the article", c.containsKey(LABEL));
		assertEquals(text, c.get(LABEL).getText());
		
		Article b = new Article(bot, LABEL);
		assertEquals(text, b.getText());
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestTimeout() throws Exception {
		

		CacheHandler c = new SimpleCache(1000);
		bot.setCacheHandler(c);
		Article a = new Article(bot, LABEL);
		String text = getRandom(8);
		a.setText(text);
		a.save();
		assertTrue("should contains the article", c.containsKey(LABEL));
		assertEquals(text, c.get(LABEL).getText());
		
		Article b = new Article(bot, LABEL);
		assertEquals(text, b.getText());
		synchronized (this) {
			wait(1000);
		}
		
		assertFalse("should not contains the article", c.containsKey(LABEL));
		Article d = new Article(bot, LABEL);
		assertEquals(text, d.getText());
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void cacheTestAttributes() throws Exception {
		

		CacheHandler c = new SimpleCache();
		bot.setCacheHandler(c);
		Article a = new Article(bot, LABEL);
		String text = getRandom(8);
		String sum = getRandom(8);
		a.setText(text);
		a.setEditSummary(sum);
		a.setMinorEdit(true);
		a.save();
		assertTrue("should contains the article", c.containsKey(LABEL));
		assertEquals(text, c.get(LABEL).getText());
		assertEquals(sum, c.get(LABEL).getEditSummary());
		assertTrue(c.get(LABEL).isMinorEdit());
		
		Article b = new Article(bot, LABEL);
		assertEquals(text, b.getText());
		assertEquals(sum, b.getEditSummary());
		assertTrue("shuld be true", b.isMinorEdit());
		
	}
	
	@After
	public final void afterTest() {
		Article b = new Article(bot, LABEL);
		try {
			b.delete();
		} catch (ActionException e) {
//			e.printStackTrace();
		} catch (ProcessException e) {
//			e.printStackTrace();
		}
	}
}
