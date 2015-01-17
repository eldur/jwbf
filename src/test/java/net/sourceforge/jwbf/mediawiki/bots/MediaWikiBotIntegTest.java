package net.sourceforge.jwbf.mediawiki.bots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.Logging;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import org.junit.Test;

public class MediaWikiBotIntegTest extends AbstractIntegTest {

  RequestMatcher revisions = ApiMatcherBuilder.of() //
      .param("action", "query") //
      .param("format", "xml") //
      .param("prop", "revisions") //
      .param("rvlimit", "1") //
      .param("rvprop", "content|comment|timestamp|user|ids|flags") //
      .param("rvdir", "older") //
      .param("titles", "A|B") //
      .build();

  RequestMatcher revision = ApiMatcherBuilder.of() //
      .param("action", "query") //
      .param("format", "xml") //
      .param("prop", "revisions") //
      .param("rvlimit", "1") //
      .param("rvprop", "content|comment|timestamp|user|ids|flags") //
      .param("rvdir", "older") //
      .param("titles", "B") //
      .build();

  @Test
  public void testreadData() {
    // GIVEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    server.request(revisions).response(TestHelper.anyWikiResponse("revisions.xml"));
    MediaWikiBot testee = new MediaWikiBot(host());

    // WHEN
    ImmutableList<SimpleArticle> result = testee.readData("A", "B");

    // THEN
    SimpleArticle articleB = getSimpleArticle();
    ImmutableList<SimpleArticle> expected = ImmutableList.of(new SimpleArticle("A"), articleB);
    GAssert.assertEquals(expected, result);
    GAssert.assertEquals(ImmutableList.of("[WARN] Article 'A' is missing"), logLinesSupplier.get());
  }

  @Test
  public void testReadDataOpt() {
    // GIVEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    server.request(revisions).response(TestHelper.anyWikiResponse("revisions.xml"));
    MediaWikiBot testee = new MediaWikiBot(host());

    // WHEN
    ImmutableList<Optional<SimpleArticle>> result = testee.readDataOpt("A", "B");

    // THEN
    SimpleArticle articleB = getSimpleArticle();
    ImmutableList<Optional<SimpleArticle>> expected =
        ImmutableList.of(Optional.<SimpleArticle>absent(), Optional.of(articleB));
    GAssert.assertEquals(expected, result);
    GAssert.assertEquals(ImmutableList.of("[WARN] Article 'A' is missing"), logLinesSupplier.get());
  }

  SimpleArticle getSimpleArticle() {
    SimpleArticle articleB = new SimpleArticle("B");
    articleB.setText("#REDIRECT [[Any]]");
    articleB.setEditSummary("#REDIRECT [[Whatever]]");
    articleB.setEditor("Any");
    articleB.setEditTimestamp(new Date(1134727050000L));
    articleB.setRevisionId("13560");
    return articleB;
  }

  @Test
  public void testReadDataOpt_single_fail() {
    // GIVEN
    server.request(revision).response(TestHelper.anyWikiResponse("revisions.xml"));
    MediaWikiBot testee = new MediaWikiBot(host());

    try {
      // WHEN
      testee.readDataOpt("B");
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      GAssert.assertStartsWith("expected one element but was", e.getMessage());
    }

  }

  @Test
  public void testReadDataOpt_single() {
    // GIVEN
    server.request(revision).response(TestHelper.anyWikiResponse("revision.xml"));
    MediaWikiBot testee = new MediaWikiBot(host());

    // WHEN
    Optional<SimpleArticle> result = testee.readDataOpt("B");

    // THEN
    assertEquals(Optional.of(getSimpleArticle()), result);
  }
}
