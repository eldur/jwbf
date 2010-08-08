/**
 * 
 */
package net.sourceforge.jwbf.mediawiki.live;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetUserinfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

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
    TestHelper.prepareLogging();
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
        registerTestedVersion(GetUserinfo.class, bot.getVersion());
    }

  }

  /**
   *
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x09() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_09));
  }
  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x10() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_10));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_11));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_12));
  }
  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_13));
  }
  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_14));
  }
  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_15));
  }

  /**
   * 
   * @throws Exception a
   */
  @Test
  public final void userInfoWikiMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_16));
  }
  /**
   * 
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
