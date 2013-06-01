package net.sourceforge.jwbf.mediawiki.live.auto;

import java.util.Collection;

import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.misc.GetRendering;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

public class RenderingTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      GetRendering.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public RenderingTest(Version v) {
    super(v, classVerifier);
  }

  @Test
  public final void render() {
    GetRendering r = new GetRendering(bot, "bert");
    Assert.assertEquals("<p>bert</p>", r.getHtml());

    // TODO more tests
    // FIXME looks strange, because we have 3 faild actions
  }
}
