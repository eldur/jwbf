package net.sourceforge.jwbf.mediawiki.actions.meta;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetVersionIntegTest extends MocoIntegTest {

  private static final Logger log = LoggerFactory.getLogger(GetVersionIntegTest.class);

  public GetVersionIntegTest(Version v) {
    super(v);
  }

  private final ImmutableMap<String, String> title() {
    return ImmutableMap.of("title", "MW " + version().getNumber().replace(".", " "));
  }

  @Test
  public void testSiteInfo() {
    // GIVEN
    // /api.php?action=query&format=xml&meta=siteinfoWithProperties
    server.request(SiteInfoIntegTest.newSiteinfoWithProperties()) //
        .response(mwFileOf(version(), "siteinfo_detail.xml"));

    // WHEN
    Siteinfo si = bot().getPerformedAction(Siteinfo.class);

    // THEN
    GAssert.assertEquals(splittedConfigOfString(ConfKey.SITEINFO, title()),
        toSortedList(si.getNamespaces()));
    GAssert.assertEquals(splittedConfigOfString(ConfKey.INTERWIKI),
        toSortedList(si.getInterwikis()));
  }

  @Test
  public void testSiteInfo_withError() {
    // GIVEN
    server.request(SiteInfoIntegTest.newSiteinfoWithProperties()) //
        .response(mwFileOf(version(), "siteinfo_fail.xml"));

    // WHEN
    Siteinfo si = bot().getPerformedAction(Siteinfo.class);

    // THEN
    assertEquals(emptyStringMap().toString(), si.getInterwikis().toString()); // XXX
    assertEquals(emptyStringMap().toString(), si.getNamespaces().toString()); // XXX
  }

  @Test
  public void testVersion() {
    // GIVEN
    server.request(SiteInfoIntegTest.newSiteInfoMatcherBuilder().build()).response(mwFileOf(version(), "siteinfo.xml"));

    // WHEN
    Version responseVersion = bot().getVersion();

    // THEN
    assertEquals(version(), responseVersion);

  }

  @Test
  public void testVersionDetails() {

    // GIVEN
    server.request(SiteInfoIntegTest.newSiteInfoMatcherBuilder().build()) //
        .response(mwFileOf(version(), "siteinfo.xml"));

    // WHEN
    GetVersion gv = bot().getPerformedAction(GetVersion.class);

    // THEN
    GAssert.assertStartsWith("http://localhost/loki/mediawiki/mw-" + version().getNumberVariation()
        + "/index.php/", gv.getBase()); // XXX
    assertEquals("first-letter", gv.getCase());
    GAssert.assertStartsWith("MediaWiki " + version().getNumber(), gv.getGenerator());
    assertEquals(confOf(ConfKey.SITENAME), gv.getSitename());

    assertEquals(confOf(ConfKey.MAINPAGE), gv.getMainpage());

  }

}
