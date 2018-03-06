package net.sourceforge.jwbf.mediawiki.actions.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dreamhead.moco.RequestMatcher;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;

public class SiteInfoIntegTest extends MocoIntegTest {

  private static final Logger log = LoggerFactory.getLogger(SiteInfoIntegTest.class);

  public SiteInfoIntegTest(Version v) {
    super(v);
  }

  public static ApiMatcherBuilder newSiteInfoMatcherBuilder() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .param("meta", "siteinfo") //
        .param("format", "xml") //
    ;
  }

  public static RequestMatcher newSiteinfoWithProperties() {
    return SiteInfoIntegTest.newSiteInfoMatcherBuilder() //
        .param("siprop", "general|namespaces|interwikimap") //
        .build();
  }

  @Test
  public void doTest() {
    // GIVEN
    // TODO json?
    server
        .request(newSiteInfoMatcherBuilder().build()) //
        .response(mwFileOf(version(), "siteinfo.xml"));
    server
        .request(newSiteinfoWithProperties()) //
        .response(mwFileOf(version(), "siteinfo_detail.xml"));

    // WHEN
    GetVersion gv = bot().getPerformedAction(GetVersion.class);

    // THEN
    assertEquals(
        "http://localhost/loki/mediawiki/mw-"
            + version().getNumberVariation()
            + "/index.php/"
            + confOf(ConfKey.MAINPAGE).replace(" ", "_"),
        gv.getBase()); // XXX

    assertEquals("first-letter", gv.getCase());
    GAssert.assertStartsWith("MediaWiki " + version().getNumber() + ".", gv.getGenerator());
    assertEquals(confOf(ConfKey.SITENAME), gv.getSitename());

    assertEquals(confOf(ConfKey.MAINPAGE), gv.getMainpage());

    Siteinfo si = bot().getPerformedAction(Siteinfo.class);
    log.debug(si.getInterwikis().toString());
    assertTrue("shuld have interwikis", si.getInterwikis().size() > 5);

    assertEquals(confOf(ConfKey.MAINPAGE), gv.getMainpage());
    log.debug(si.getNamespaces().toString());
    assertTrue("shuld have namespaces", si.getNamespaces().size() > 15);
  }
}
