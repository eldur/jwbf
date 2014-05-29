package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.fail;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.Logging;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;

public class TitleQueryIntegTest extends AbstractIntegTest {

  RequestMatcher embeddedinTwo = ApiMatcherBuilder.of() //
      .param("eicontinue", "10|Babel|37163") //
      .param("action", "query") //
      .param("format", "xml") //
      .param("eilimit", "50") //
      .param("einamespace", "2") //
      .param("eititle", "Template:Babel") //
      .param("list", "embeddedin") //
      .build();

  static RequestMatcher embeddedinOne() {
    return ApiMatcherBuilder.of() //
        .param("action", "query") //
        .param("format", "xml") //
        .param("eilimit", "50") //
        .param("einamespace", "2") //
        .param("eititle", "Template:Babel") //
        .param("list", "embeddedin") //
        .build();
  }

  @Test
  public void testEndless_fail() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("embeddedin_2.xml"));
    server.request(embeddedinOne()).response(TestHelper.anyWikiResponse("embeddedin_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    TitleQuery<String> testee = new TemplateUserTitles(bot, "Template:Babel", MediaWiki.NS_USER);

    try {
      ImmutableList.copyOf(testee.lazy());
      fail();
    } catch (IllegalStateException e) {
      // THEN
      GAssert.assertStartsWith("invalid status: HTTP/1.1 400 Bad Request;", e.getMessage());
    }

  }

  @Test
  public void testDuplication() {

    // GIVEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    server.request(embeddedinOne()).response(TestHelper.anyWikiResponse("embeddedin_1.xml"));
    server.request(embeddedinOne()).response(TestHelper.anyWikiResponse("embeddedin_1.xml"));
    server.request(embeddedinOne()).response(TestHelper.anyWikiResponse("embeddedin_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    TemplateUserTitles testee = new TemplateUserTitles(bot, "Template:Babel", MediaWiki.NS_USER);
    testee.getCopyOf(15);

    // THEN
    String warn = "[WARN] previous response has same payload";
    GAssert.assertEquals(ImmutableList.of(warn, warn), logLinesSupplier.get());

  }

  @Test
  public void testLazy() {

    // GIVEN
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    TitleQuery<String> testee = new TemplateUserTitles(bot, "Template:Babel", MediaWiki.NS_USER);
    testee.lazy();

    // THEN
    // will fail if lazy is not lazy ;-)

  }

}
