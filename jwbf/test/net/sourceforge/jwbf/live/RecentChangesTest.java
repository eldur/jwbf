/**
 * 
 */
package net.sourceforge.jwbf.live;

import java.util.Iterator;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;
import net.sourceforge.jwbf.contentRep.mw.Version;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas
 *
 */
public class RecentChangesTest extends LiveTestFather {
	private MediaWikiBot bot = null;
	private static final int COUNT = 52;
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);

//		prepareTestWikis();
	}
	
	public static final void prepareTestWikis() throws Exception {
		SimpleArticle a = new SimpleArticle("test", "0");
		MediaWikiBot bot;
		 
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		
		for (int i = 0; i < 60; i++) {
			a.setLabel("Test " + i);
			bot.writeContent(a);
		}
		
		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_11_pass"));
		
		for (int i = 0; i < 60; i++) {
			a.setLabel("Test " + i);
			bot.writeContent(a);
		}
//		
//		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
//		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
//		
//		for (int i = 0; i < 60; i++) {
//			a.setLabel("Test " + i);
//			bot.writeContent(a);
//		}
//		
//
//		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
//		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
//		
//		for (int i = 0; i < 60; i++) {
//			a.setLabel("Test " + i);
//			bot.writeContent(a);
//		}
		
		
		
		
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void recentChangesWikiMW1_09() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		Iterator<String> is = bot.getRecentchangesTitles(COUNT).iterator();
		if (!is.hasNext()) {
			change(bot);
			is = bot.getRecentchangesTitles(COUNT).iterator();
		}
		int i = 0;
		while (is.hasNext()) {
			String out = is.next();
			System.out.println("-- " + out);
			i++;
			if (i > COUNT -1) {
				break;
			}
		}
		
		Assert.assertTrue("i is: " + i , i > COUNT -1);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_09.equals(bot.getVersion()));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void recentChangesWikiMW1_10() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));
		Iterator<String> is = bot.getRecentchangesTitles(COUNT).iterator();
		if (!is.hasNext()) {
			change(bot);
			is = bot.getRecentchangesTitles(COUNT).iterator();
		}
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT -1) {
				break;
			}
		}
		
		Assert.assertTrue("i is: " + i , i > COUNT -1);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_10.equals(bot.getVersion()));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void recentChangesWikiMW1_11() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));
		Iterator<String> is = bot.getRecentchangesTitles(COUNT).iterator();
		if (!is.hasNext()) {
			change(bot);
			is = bot.getRecentchangesTitles(COUNT).iterator();
		}
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT -1) {
				break;
			}
		}
		
		Assert.assertTrue("i is: " + i , i > COUNT -1);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_11.equals(bot.getVersion()));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void recentChangesWikiMW1_12() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));
		Iterator<String> is = bot.getRecentchangesTitles(COUNT).iterator();
		if (!is.hasNext()) {
			change(bot);
			is = bot.getRecentchangesTitles(COUNT).iterator();
		}
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT -1) {
				break;
			}
		}
		
		Assert.assertTrue("i is: " + i , i > COUNT -1);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_12.equals(bot.getVersion()));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void recentChangesWikiMW1_13() throws Exception {
//		prepareTestWikis();
		bot = new MediaWikiBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		Iterator<String> is = bot.getRecentchangesTitles(COUNT).iterator();
		if (!is.hasNext()) {
			change(bot);
			is = bot.getRecentchangesTitles(COUNT).iterator();
		}
		int i = 0;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT -1) {
				break;
			}
		}
		
		Assert.assertTrue("i is: " + i , i > COUNT -1);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
	}
	
	private final void change(MediaWikiBot bot) throws ActionException, ProcessException {
//		SimpleArticle a = new SimpleArticle("Change", "0");
//		for (int i = 0; i < COUNT + 1; i++) {
//			a.setLabel("Cahnge " + i);
//			a.setText(System.currentTimeMillis() + "");
//			bot.writeContent(a);
//		}
	}
}
