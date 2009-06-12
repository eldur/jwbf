/**
 * 
 */
package net.sourceforge.jwbf.live.mediawiki;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.meta.GetSiteinfo;
import net.sourceforge.jwbf.bots.MediaWikiAdapterBot;
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
	private MediaWikiAdapterBot bot = null;

	/**
	 * 
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		addInitSupporterVersions(GetSiteinfo.class);
	}
	private void testDetails(MediaWikiBot bot, String userName) throws Exception {
		Userinfo u = bot.getUserinfo();
		Assert.assertEquals(userName, u.getUsername());
		
		switch (bot.getSiteinfo().getVersion()) {
		case MW1_09:
		case MW1_10:	
			break;

		default:
			Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
			Assert.assertTrue("User has no read rights", u.getRights().contains("read"));
		}
		
		registerTestedVersion(GetSiteinfo.class, bot.getVersion());
		
	}

	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x09() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"), getValue("wikiMW1_09_pass"));
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_09_user"));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x10() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_10_url"));
		bot.login(getValue("wikiMW1_10_user"), getValue("wikiMW1_10_pass"));

		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_10_user"));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x11() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_11_url"));
		bot.login(getValue("wikiMW1_11_user"), getValue("wikiMW1_11_pass"));

		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_11_user"));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x12() throws Exception {
		
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_12_url"));
		bot.login(getValue("wikiMW1_12_user"), getValue("wikiMW1_12_pass"));

		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_12_user"));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x13() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_13_url"));
		bot.login(getValue("wikiMW1_13_user"), getValue("wikiMW1_13_pass"));
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_13_user"));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x14() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_14_user"));
	}
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x15() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_15_url"));
		bot.login(getValue("wikiMW1_15_user"), getValue("wikiMW1_15_pass"));
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
		testDetails(bot, getValue("wikiMW1_15_user"));
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void userInfoWikiMW1x14Rights() throws Exception {
		bot = new MediaWikiAdapterBot(getValue("wikiMW1_14_url"));
		
		
		Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
		
		Userinfo u = bot.getUserinfo();
		Assert.assertNotSame("unknown", u.getUsername());
		Assert.assertNotSame(getValue("wikiMW1_14_user"), u.getUsername());
		u = bot.getUserinfo();
		Assert.assertNotSame("unknown", u.getUsername());
		Assert.assertNotSame(getValue("wikiMW1_14_user"), u.getUsername());
		bot.login(getValue("wikiMW1_14_user"), getValue("wikiMW1_14_pass"));
		u = bot.getUserinfo();
		Assert.assertEquals(getValue("wikiMW1_14_user"), u.getUsername());
			Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
			Assert.assertTrue("User has no read rights", u.getRights().contains("read"));
		
	
	}
	
}
