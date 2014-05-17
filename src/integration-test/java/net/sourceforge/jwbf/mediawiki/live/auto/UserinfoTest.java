/**
 *
 */
package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiPass;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getWikiUser;

import java.util.Collection;

import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetUserinfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Thomas
 */
public class UserinfoTest extends ParamHelper {

  private Version v;

  public UserinfoTest(Version v) {
    super(v, classVerifier);
    this.v = v;
  }

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      GetUserinfo.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  private void testDetails(MediaWikiBot bot, String userName) {
    Userinfo u = bot.getUserinfo();
    Assert.assertEquals(userName, u.getUsername());

    Assert.assertFalse("User has no groups", u.getGroups().isEmpty());
    Assert.assertTrue("User has no read rights", u.getRights().contains("read"));

  }

  @Test
  public final void userInfo() {
    testDetails(bot, getWikiUser(v));
  }

  @Ignore("to complex, use recored test")
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
    Assert.assertTrue("User has no read rights", u.getRights().contains("read"));

  }

}
