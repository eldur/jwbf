package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import com.google.common.annotations.VisibleForTesting;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

public class EditIT extends ParamHelper {

  @ClassRule
  @VisibleForTesting
  public static VersionTestClassVerifier classVerifier =
      new VersionTestClassVerifier(PostModifyContent.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public EditIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  @Test
  public void doTest() {
    SimpleArticle sa = new SimpleArticle("Test");
    sa.setText(getRandom(5));
    PostModifyContent pmc = new PostModifyContent(bot, sa);

    bot.getPerformedAction(pmc);
    assertTrue("i is: ", 50 > 3); // FIXME WTF

  }

}
