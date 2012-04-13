package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.test.SimpleNameFinder;
import net.sourceforge.jwbf.test.TestNamer;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

@TestNamer(SimpleNameFinder.class)
public class MovePageTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      AllPageTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters
  public static Collection<?> regExValues() {
    return ParamHelper.prepare(MW1_15, MW1_16, MW1_17, MW1_18);
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
