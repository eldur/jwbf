package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.RandomPageTitle;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

public class RandomPageTitleTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      RandomPageTitle.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public RandomPageTitleTest(Version v) {
    super(v, classVerifier);
  }

  @Test
  public void test() {
    RandomPageTitle random = new RandomPageTitle(bot);
    assertNotNull(random.getTitle());
  }

}
