package net.sourceforge.jwbf.mediawiki.actions.queries;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.fail;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.Logging;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;

public class TitleQueryIntegTest extends AbstractIntegTest {

  RequestMatcher embeddedinTwo = AbstractIntegTest.onlyOnce(and(by(uri("/api.php")), //
      eq(query("eicontinue"), "10|Babel|37163"), //
      eq(query("action"), "query"), //
      eq(query("format"), "xml"), //
      eq(query("eilimit"), "50"), //
      eq(query("einamespace"), "2"), //
      eq(query("eititle"), "Template:Babel"), //
      eq(query("list"), "embeddedin") //
  ));

  static RequestMatcher embeddedinOne() {
    return AbstractIntegTest.onlyOnce(and(by(uri("/api.php")), //
        eq(query("action"), "query"), //
        eq(query("format"), "xml"), //
        eq(query("eilimit"), "50"), //
        eq(query("einamespace"), "2"), //
        eq(query("eititle"), "Template:Babel"), //
        eq(query("list"), "embeddedin") //
    ));
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
