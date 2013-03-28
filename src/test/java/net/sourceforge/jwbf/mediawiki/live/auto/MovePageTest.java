package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

@Ignore("because incomplete")
public class MovePageTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      AllPageTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public MovePageTest(Version v) {
    super(v);
  }

  @Test
  public final void test() {
    Set<String> rights = bot.getUserinfo().getRights();
    assertTrue("bot has no move rights", rights.contains("move"));
    // TODO complete
    String oldPage = "old";
    String newPage = "new";
    Article a = bot.getArticle(oldPage);
    // a.delete();
    a.setText("A");
    a.save();
    fail("rev: " + a.getRevisionId());
    // bot.performAction(new MovePage(bot, "old", "new", "no reason", true,
    // true));
    // fail("ups " + rights);
  }
}
