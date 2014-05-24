package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.editing.MovePage;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

public class MovePageTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      MovePage.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  private final String oldPage = "old";
  private final String newPage = "new";
  private final String lastPage = "last";

  public MovePageTest(Version v) {
    super(v, classVerifier);
  }

  @Test
  public final void test() {
    Set<String> rights = bot.getUserinfo().getRights();
    assertTrue("bot has no move rights", rights.contains("move"));

    Article a = bot.getArticle(oldPage);
    String randomText = TestHelper.getRandomAlpha(23);
    a.setText(randomText);
    a.save();
    bot.performAction(new MovePage(bot, oldPage, newPage, "no reason", false, true));
    Article b = bot.getArticle(newPage);
    assertFalse(b.getText().startsWith("#"));
    assertEquals(randomText, b.getText());
    randomText = TestHelper.getRandomAlpha(23);
    b.setText(randomText);
    b.save();
    bot.performAction(new MovePage(bot, newPage, lastPage, "no reason", false, false));
    Article c = bot.getArticle(lastPage);
    assertEquals(randomText, c.getText());
    b = bot.getArticle(newPage);
    assertTrue(b.getText().startsWith("#"));
  }

  @Before
  public void before() {
    bot.delete(newPage);
    bot.delete(oldPage);
    bot.delete(lastPage);
  }
}
