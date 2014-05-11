package net.sourceforge.jwbf.mediawiki.actions.queries;

import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.fail;

import java.util.Collection;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import net.sourceforge.jwbf.mediawiki.RequestMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.SiteInfoIntegTest;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;

public class AllPageTitlesIntegTest extends MocoIntegTest {

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public AllPageTitlesIntegTest(Version v) {
    super(v);
  }

  RequestMatcherBuilder newBaseMatcher() {
    return new RequestMatcherBuilder() //
        .with(by(uri("/api.php"))) //
        .with(eq(query("apfilterredir"), "nonredirects")) //
        .with(eq(query("format"), "xml")) //
        .with(eq(query("list"), "allpages")) //
        .with(eq(query("aplimit"), "50")) //
    ;
  }

  RequestMatcher allpages0 = newBaseMatcher().build();

  RequestMatcher allpages1 = newBaseMatcher() //
      .with(eq(query("apfrom"), confOf(ConfKey.ALL_PAGE_CONT_1))) //
      .build();

  RequestMatcher allpages2 = newBaseMatcher() //
      .with(eq(query("apfrom"), confOf(ConfKey.ALL_PAGE_CONT_2))) //
      .build();

  @Test
  public void doTest() {

    // GIVEN
    // TODO json?
    server.request(SiteInfoIntegTest.siteinfo).response(mwFileOf(version(), "siteinfo_detail.xml"));
    server.request(allpages2).response(mwFileOf(version(), "allPageTitles2.xml"));
    server.request(allpages1).response(mwFileOf(version(), "allPageTitles1.xml"));
    server.request(allpages0).response(mwFileOf(version(), "allPageTitles0.xml"));

    // WHEN
    ImmutableList<String> actual = new AllPageTitles(bot()).getCopyOf(3);

    // THEN
    ImmutableList<String> expected = ImmutableList.of( //
        confOf(ConfKey.ALL_PAGE_0), //
        confOf(ConfKey.ALL_PAGE_1), //
        confOf(ConfKey.ALL_PAGE_2));
    GAssert.assertEquals(expected, actual);

  }

  @Test
  public void doFail() {

    // GIVEN
    // nothing
    try {
      // WHEN
      new AllPageTitles(bot()).getCopyOf(2);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      GAssert.assertStartsWith("invalid status: HTTP", e.getMessage());
    }
  }

}
