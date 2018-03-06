package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;

public class AllPageTitlesIntegTest extends MocoIntegTest {

  public AllPageTitlesIntegTest(Version v) {
    super(v);
  }

  ApiMatcherBuilder newBaseMatcher() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .param("apfilterredir", "nonredirects") //
        .paramNewContinue(version()) //
        .param("format", "xml") //
        .param("list", "allpages") //
        .param("aplimit", "50") //
    ;
  }

  RequestMatcher allpages0 = newBaseMatcher().build();

  RequestMatcher allpages1 =
      newBaseMatcher() //
          .param("apfrom", confOf(ConfKey.ALL_PAGE_CONT_1)) //
          .build();

  RequestMatcher allpages2 =
      newBaseMatcher() //
          .param("apfrom", confOf(ConfKey.ALL_PAGE_CONT_2)) //
          .build();

  @Test
  public void test() {

    // GIVEN
    // TODO json?
    applySiteinfoXmlToServer();
    server.request(allpages2).response(mwFileOf(version(), "allPageTitles2.xml"));
    server.request(allpages1).response(mwFileOf(version(), "allPageTitles1.xml"));
    server.request(allpages0).response(mwFileOf(version(), "allPageTitles0.xml"));

    // WHEN
    ImmutableList<String> actual = new AllPageTitles(bot()).getCopyOf(3);

    // THEN
    ImmutableList<String> expected =
        ImmutableList.of( //
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
