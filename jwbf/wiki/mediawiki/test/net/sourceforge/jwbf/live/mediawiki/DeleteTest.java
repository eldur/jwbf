/**
 * 
 */
package net.sourceforge.jwbf.live.mediawiki;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.editing.PostDelete;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.ContentAccessable;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas
 *
 */
public class DeleteTest extends LiveTestFather {
	private MediaWikiBot bot = null;
	private static final int COUNT = 1;
	
	/***
	 * 
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		addInitSupporterVersions(PostDelete.class);
		
		
	}
	private void prepare(MediaWikiBot bot) throws Exception {
		SimpleArticle a = new SimpleArticle();
		
		for (int i = 0; i < COUNT; i++) {
			a.setTitle("Delete " + i);
			a.setText(getRandom(23));
			bot.writeContent(a);
		}
	}
	
	private void delete(MediaWikiBot bot) throws ActionException, ProcessException {
		
		for (int i = 0; i < COUNT; i++) {
			bot.postDelete("Delete " + i);
		}
	}
	
	private void test(MediaWikiBot bot) throws ActionException, ProcessException {
	
		for (int i = 0; i < COUNT; i++) {
			ContentAccessable ca = bot.readContent("Delete " + i);
			
			Assert.assertTrue("textlength of Delete " 
					+ i + " is greater then 0 (" + ca.getText().length() 
					+ ")", ca.getText().length() == 0);
			registerTestedVersion(PostDelete.class, bot.getVersion());
			
			
		}
	}
	
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test(expected = VersionException.class)
	public final void deleteWikiMW1x09Fail() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_09, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
		registerUnTestedVersion(PostDelete.class, bot.getVersion());
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test(expected = VersionException.class)
	public final void deleteWikiMW1x10Fail() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_10, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
		registerUnTestedVersion(PostDelete.class, bot.getVersion());
		
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test(expected = VersionException.class)
	public final void deleteWikiMW1x11Fail() throws Exception {
		bot = getMediaWikiBot(Version.MW1_11, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
		registerUnTestedVersion(PostDelete.class, bot.getVersion());
		
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void deleteWikiMW1x12() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_12, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
		
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void deleteWikiMW1x13() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_13, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
		
		prepare(bot);
		delete(bot);
		test(bot);
		
		
	}
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void deleteWikiMW1x14() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_14, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
		
		prepare(bot);
		delete(bot);
		test(bot);
	}
	
	/**
	 * Test.
	 * @throws Exception a
	 */
	@Test
	public final void deleteWikiMW1x15() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_15, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
		
		prepare(bot);
		delete(bot);
		test(bot);
	}
}
