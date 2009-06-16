/**
 * 
 */
package net.sourceforge.jwbf.live.mediawiki;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.meta.GetUserinfo;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.Userinfo;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas
 *
 */
public class UserinfoTest extends LiveTestFather {
	private MediaWikiBot bot = null;

	/**
	 * 
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		addInitSupporterVersions(GetUserinfo.class);
	}
	private void testDetails(MediaWikiBot bot, String userName) throws Exception {
		Userinfo u = bot.getUserinfo();
		Assert.assertEquals(userName, u.getUsername());
		
		switch (bot.getVersion()) {
		case MW1_09:
		case MW1_10:	
			break;

		default:
			Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
			Assert.assertTrue("User has no read rights", u.getRights().contains("read"));
		}
		
		registerTestedVersion(GetUserinfo.class, bot.getVersion());
		
	}

	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x09() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_09, true);
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_09));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x10() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_10, true);

		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_10));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x11() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_11, true);

		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_11));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x12() throws Exception {
		
		bot = getMediaWikiBot(Version.MW1_12, true);

		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_12));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x13() throws Exception {
		bot = getMediaWikiBot(Version.MW1_13, true);
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_13));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x14() throws Exception {
		bot = getMediaWikiBot(Version.MW1_14, true);
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_14));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x15() throws Exception {
		bot = getMediaWikiBot(Version.MW1_15, true);
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
		testDetails(bot, getWikiUser(Version.MW1_15));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x14Rights() throws Exception {
		bot = getMediaWikiBot(Version.MW1_14, false);
		
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
		
		Userinfo u = bot.getUserinfo();
		Assert.assertNotSame("unknown", u.getUsername());
		Assert.assertNotSame(getWikiUser(Version.MW1_14), u.getUsername());
		u = bot.getUserinfo();
		Assert.assertNotSame("unknown", u.getUsername());
		Assert.assertNotSame(getWikiUser(Version.MW1_14), u.getUsername());
		bot.login(getWikiUser(Version.MW1_14), getWikiPass(Version.MW1_14));
		u = bot.getUserinfo();
		Assert.assertEquals(getWikiUser(Version.MW1_14), u.getUsername());
			Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
			Assert.assertTrue("User has no read rights", u.getRights().contains("read"));
		
	
	}
	
}
