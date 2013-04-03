package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.meta.Siteinfo;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

@Slf4j
public class SiteInfoTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      GetVersion.class, Siteinfo.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public SiteInfoTest(Version v) {
    super(v, classVerifier);
  }

  @Test
  public void doTest() {

    GetVersion gv = new GetVersion();
    bot.performAction(gv);

    log.info(gv.getBase());
    assertTrue(gv.getBase().length() > 0);

    log.info(gv.getCase());
    assertTrue(gv.getCase().length() > 0);

    log.info(gv.getGenerator());
    assertTrue(gv.getGenerator().length() > 0);

    log.info(gv.getMainpage());
    assertTrue(gv.getMainpage().length() > 0);

    log.info(gv.getSitename());
    assertTrue(gv.getSitename().length() > 0);

    Siteinfo si = new Siteinfo();
    bot.performAction(si);
    log.info(si.getInterwikis().toString());
    assertTrue("shuld have interwikis", si.getInterwikis().size() > 5);

    log.info(si.getNamespaces().toString());
    assertTrue("shuld have namespaces", si.getNamespaces().size() > 15);
    // registerTestedVersion(Siteinfo.class, v); // TODO

  }

}
