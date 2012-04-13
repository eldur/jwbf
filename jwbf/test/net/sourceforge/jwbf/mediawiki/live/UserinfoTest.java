/**
 * 
 */
package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiPass;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiUser;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetUserinfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

/**
 * @author Thomas
 * 
 */
public class UserinfoTest extends AbstractMediaWikiBotTest {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      GetUserinfo.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  private void testDetails(MediaWikiBot bot, String userName) {
    Userinfo u = bot.getUserinfo();
    Assert.assertEquals(userName, u.getUsername());

    Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
    Assert
        .assertTrue("User has no read rights", u.getRights().contains("read"));

  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void userInfoWikiMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_15.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_15));
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void userInfoWikiMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_16.equals(bot.getVersion()));
    testDetails(bot, getWikiUser(Version.MW1_16));
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void userInfoWikiMW1x17Rights() throws Exception {
    bot = getMediaWikiBot(Version.MW1_17, false);

    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_17.equals(bot.getVersion()));

    Userinfo u = bot.getUserinfo();
    Assert.assertNotSame("unknown", u.getUsername());
    Assert.assertNotSame(getWikiUser(Version.MW1_17), u.getUsername());
    u = bot.getUserinfo();
    Assert.assertNotSame("unknown", u.getUsername());
    Assert.assertNotSame(getWikiUser(Version.MW1_17), u.getUsername());
    bot.login(getWikiUser(Version.MW1_17), getWikiPass(Version.MW1_17));
    u = bot.getUserinfo();
    Assert.assertEquals(getWikiUser(Version.MW1_17), u.getUsername());
    Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
    Assert
        .assertTrue("User has no read rights", u.getRights().contains("read"));

  }

}
