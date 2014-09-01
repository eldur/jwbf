package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class LoginTest extends ParamHelper {

  public LoginTest(Version v) {
    super(BotFactory.getMediaWikiBot(v, false));
  }

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  /**
   * Test login on a Mediawiki.
   */
  @Test
  public final void login() {
    bot.login(BotFactory.getWikiUser(version()), BotFactory.getWikiPass(version()));
    assertTrue(bot.isLoggedIn());
  }

  /**
   * Test FAIL login on Mediawiki. TODO change exception test, should fail if no route to test host
   */
  @Test
  public final void loginFail() {
    try {
      bot.login("Klhjfd", "4sdf");
      fail();
    } catch (ActionException pe) {
      assertTrue(ImmutableSet.of("Throttled", "No such User").contains(pe.getMessage()));
    }
  }

}
