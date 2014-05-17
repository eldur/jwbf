package net.sourceforge.jwbf.mediawiki.actions.meta;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import com.github.dreamhead.moco.RequestMatcher;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteInfoIntegTest extends MocoIntegTest {

  private static final Logger log = LoggerFactory.getLogger(SiteInfoIntegTest.class);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public SiteInfoIntegTest(Version v) {
    super(v);
  }

  public static final RequestMatcher siteinfo = and(by(uri("/api.php")),
      eq(query("action"), "query"), eq(query("format"), "xml"), eq(query("meta"), "siteinfo"));

  @Test
  public void doTest() {
    // GIVEN
    // TODO json?
    server.request(siteinfo).response(mwFileOf(version(), "siteinfo_detail.xml"));
    // WHEN
    GetVersion gv = bot().getPerformedAction(GetVersion.class);

    // THEN
    assertEquals("http://localhost/loki/mediawiki/mw-" + version().getNumberVariation()
        + "/index.php/" + confOf(ConfKey.MAINPAGE).replace(" ", "_"), gv.getBase()); // XXX

    assertEquals("first-letter", gv.getCase());
    GAssert.assertStartsWith("MediaWiki " + version().getNumber() + ".", gv.getGenerator());
    assertEquals(confOf(ConfKey.SITENAME), gv.getSitename());

    assertEquals(confOf(ConfKey.MAINPAGE), gv.getMainpage());

    Siteinfo si = bot().getPerformedAction(Siteinfo.class);
    log.info(si.getInterwikis().toString());
    assertTrue("shuld have interwikis", si.getInterwikis().size() > 5);

    assertEquals(confOf(ConfKey.MAINPAGE), gv.getMainpage());
    log.info(si.getNamespaces().toString());
    assertTrue("shuld have namespaces", si.getNamespaces().size() > 15);

  }

}
