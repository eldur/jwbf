package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;

public class LogEventsIntegTest extends MocoIntegTest {

  public LogEventsIntegTest(MediaWiki.Version version) {
    super(version);
  }

  ApiMatcherBuilder newBaseMatcher() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .paramNewContinue(version()) //
        .param("format", "xml") //
        .param("lelimit", "1") //
        .param("letype", "delete") //
        .param("list", "logevents") //
    ;
  }

  RequestMatcher logEvents0 =
      newBaseMatcher() //
          .build();

  @Test
  public void test() {

    // GIVEN
    applySiteinfoXmlToServer();
    server.request(logEvents0).response(mwFileOf(version(), "logEventsDelete0.xml"));
    if (version().greaterEqThen(MediaWiki.Version.MW1_23)) {
      server
          .request(
              newBaseMatcher() //
                  .param("lecontinue", "20141005135252|17680") //
                  .build())
          .response(mwFileOf(version(), "logEventsDelete1.xml"));
      server
          .request(
              newBaseMatcher() //
                  .param("lecontinue", "20141005135252|17678") //
                  .build())
          .response(mwFileOf(version(), "logEventsDelete2.xml"));
    }

    // WHEN
    ImmutableList<LogItem> actual = new LogEvents(bot(), 1, LogEvents.DELETE).getCopyOf(3);

    // THEN
    ImmutableList<String> expected = splittedConfigOfString(ConfKey.LOGEVENTS_DELETE_PAGES);
    GAssert.assertEquals(
        expected,
        FluentIterable.from(actual) //
            .transform(LogEvents.toTitles()) //
            .toList());
  }

  @Test
  public void doFail() {
    // GIVEN
    // nothing
    try {
      // WHEN
      new LogEvents(bot(), LogEvents.DELETE).getCopyOf(2);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      GAssert.assertStartsWith("invalid status: HTTP", e.getMessage());
    }
  }
}
