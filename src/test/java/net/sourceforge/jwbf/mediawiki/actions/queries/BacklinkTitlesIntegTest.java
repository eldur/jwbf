package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.fail;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import org.junit.Test;

public class BacklinkTitlesIntegTest extends MocoIntegTest {

  public BacklinkTitlesIntegTest(Version v) {
    super(v);
  }

  ApiMatcherBuilder newBaseMatcher() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .param("blfilterredir", "all") //
        .param("bllimit", "50") //
        .param("bltitle", "Test") //
        .param("format", "xml") //
        .param("list", "backlinks") //
        .paramNewContinue(version()) //
        ;
  }

  RequestMatcher backlinks0 = newBaseMatcher().build();

  RequestMatcher backlinks1 = newBaseMatcher() //
      .param("blcontinue", confOf(ConfKey.BACKLINKS_CONT_1)) //
      .build();

  RequestMatcher backlinks2 = newBaseMatcher() //
      .param("blcontinue", confOf(ConfKey.BACKLINKS_CONT_2)) //
      .build();

  @Test
  public void test() {

    // GIVEN
    // TODO json?
    applySiteinfoXmlToServer();
    server.request(backlinks2).response(mwFileOf(version(), "backlinkTitles2.xml"));
    server.request(backlinks1).response(mwFileOf(version(), "backlinkTitles1.xml"));
    server.request(backlinks0).response(mwFileOf(version(), "backlinkTitles0.xml"));

    // WHEN
    ImmutableList<String> actual = new BacklinkTitles(bot(), "Test").getCopyOf(14);

    // THEN
    ImmutableList<String> expected = splittedConfigOfString(ConfKey.BACKLINKS_PAGES);
    GAssert.assertEquals(expected, actual);
  }

  @Test
  public void doFail() {
    // GIVEN
    // nothing
    try {
      // WHEN
      new BacklinkTitles(bot(), "Test").getCopyOf(2);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      GAssert.assertStartsWith("invalid status: HTTP", e.getMessage());
    }
  }

}
