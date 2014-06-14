package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLogin;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

public class LoginTest extends ParamHelper {

  private final Version v;

  public LoginTest(Version v) {
    super(v, classVerifier);
    this.v = v;
  }

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  @ClassRule
  public static VersionTestClassVerifier classVerifier =
      new VersionTestClassVerifier(PostLogin.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  /**
   * Test login on a Mediawiki.
   */
  @Test
  public final void login() {

    bot = BotFactory.getMediaWikiBot(v, false);
    bot.login(BotFactory.getWikiUser(v), BotFactory.getWikiPass(v));
    assertTrue(bot.isLoggedIn());
  }

  /**
   * Test FAIL login on Mediawiki. TODO change exception test, should fail if no route to test host
   */
  @Test
  public final void loginFail() {
    try {
      MediaWikiBot bot = BotFactory.getMediaWikiBot(v, false);
      bot.login("Klhjfd", "4sdf");
      fail();
    } catch (ActionException pe) {
      assertTrue(ImmutableSet.of("Throttled", "No such User").contains(pe.getMessage()));
    }
  }

  /**
   * Test login where the wiki is in a subfolder, like www.abc.com/wiki .
   */
  @Test(expected = IllegalArgumentException.class)
  public final void loginUrlformatsFail() {

    String defektUrl = BotFactory.getWikiUrlOrSkip(v);
    int lastSlash = defektUrl.lastIndexOf("/");
    defektUrl = defektUrl.substring(0, lastSlash);
    assertFalse("shuld not end with .php", defektUrl.endsWith(".php"));
    MediaWikiBot bot = new MediaWikiBot(defektUrl);
    bot.login(BotFactory.getWikiUser(v), BotFactory.getWikiPass(v));
  }

}
