package net.sourceforge.jwbf.mediawiki.actions.meta;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableMap;

@Slf4j
public class GetVersionIntegTest extends MocoIntegTest {

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public GetVersionIntegTest(Version v) {
    super(v);
  }

  private final RequestMatcher siteinfo = and(by(uri("/api.php")),
      eq(query("siprop"), "general|namespaces|interwikimap"), eq(query("action"), "query"),
      eq(query("format"), "xml"), eq(query("meta"), "siteinfo"));

  // /api.php?action=query&format=xml&meta=siteinfo
  private final RequestMatcher siteinfoShort = and(by(uri("/api.php")),
      eq(query("action"), "query"), eq(query("format"), "xml"), eq(query("meta"), "siteinfo"));

  private final ImmutableMap<String, String> title() {
    return ImmutableMap.of("title", "MW " + version().getNumber().replace(".", " "));
  }

  @Test
  public void testSiteInfo() {
    // GIVEN
    // /api.php?action=query&format=xml&meta=siteinfo
    server.request(siteinfo).response(mwFileOf(version(), "siteinfo_detail.xml"));

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
    server.request(siteinfo).response(mwFileOf(version(), "siteinfo_fail.xml"));

    // WHEN
    Siteinfo si = bot().getPerformedAction(Siteinfo.class);

    // THEN
    assertEquals(emptyStringMap().toString(), si.getInterwikis().toString()); // XXX
    assertEquals(emptyStringMap().toString(), si.getNamespaces().toString()); // XXX
  }

  @Test
  public void testVersion() {
    // GIVEN
    server.request(siteinfoShort).response(mwFileOf(version(), "siteinfo.xml"));

    // WHEN
    Version responseVersion = bot().getVersion();

    // THEN
    assertEquals(version(), responseVersion);

  }

  @Test
  public void testVersionDetails() {

    // GIVEN
    server.request(siteinfoShort).response(mwFileOf(version(), "siteinfo.xml"));

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
