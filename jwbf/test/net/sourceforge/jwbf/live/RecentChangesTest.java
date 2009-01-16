/**
 * 
 */
package net.sourceforge.jwbf.live;

import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mw.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

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
	private static final int COUNT = 13;
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);

//		prepareTestWikis();
	}
	
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void recentChangesWikiMW1_09() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		doTest(bot);
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
		doTest(bot);
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
		doTest(bot);
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
		doTest(bot);
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
		doTest(bot);
		Assert.assertTrue( "Wrong Wiki Version " + bot.getVersion() , Version.MW1_13.equals(bot.getVersion()));
	}
	private final void doTest(MediaWikiBot bot) throws ActionException,
			ProcessException {
		Iterator<String> is = bot.getRecentchangesTitles().iterator();
		int i = 0;
		boolean hasNotEnough = true;
		while (is.hasNext()) {
			is.next();
			i++;
			if (i > COUNT - 1) {
				hasNotEnough = false;
				break;
			}
		}
		if (hasNotEnough) {
			change(bot);
		}
		Vector<Integer> vi = new Vector<Integer>();
		try {
			is = bot.getRecentchangesTitles().iterator();

			i = 0;
			vi.clear();
			for (int j = 0; j < COUNT; j++) {

				vi.add(j);

			}

			while (is.hasNext()) {
				String s = is.next();
				int x = Integer.parseInt(s.split(" ")[1]);
				// System.out.println(vi);
				// System.out.println("rm " + x );
				vi.remove(new Integer(x));
				i++;
				if (i > COUNT) {
					break;
				}
			}
			if (!vi.isEmpty()) {
				throw new Exception();
			}
		} catch (Exception e) {
			change(bot);
			is = bot.getRecentchangesTitles().iterator();

			i = 0;
			vi.clear();
			for (int j = 0; j < COUNT; j++) {

				vi.add(j);

			}

			while (is.hasNext()) {
				String s = is.next();
				int x = Integer.parseInt(s.split(" ")[1]);
				// System.out.println(vi);
				// System.out.println("rm " + x );
				vi.remove(new Integer(x));
				i++;
				if (i > COUNT) {
					break;
				}
			}
		}
		Assert.assertTrue("shuld be empty but is : " + vi, vi.isEmpty());
		Assert.assertTrue("i is: " + i, i > COUNT - 1);
	}
	private final void change(MediaWikiBot bot) throws ActionException, ProcessException {
		SimpleArticle a = new SimpleArticle("Change", "0");
		for (int i = 0; i < COUNT + 1; i++) {
			a.setLabel("Change " + i);
			a.setText(getRandom(255));
			bot.writeContent(a);
		}
	}
}
