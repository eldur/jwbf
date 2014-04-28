package net.sourceforge.jwbf.mediawiki.actions.queries;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Test;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class RecentchangeTitlesIntegTest extends AbstractIntegTest {

  RequestMatcher embeddedinTwo = AbstractIntegTest.onlyOnce(and(by(uri("/api.php")), //
      eq(query("action"), "query"), //
      eq(query("format"), "xml"), //
      eq(query("rclimit"), "50"), //
      eq(query("rcnamespace"), "0") //
      ));

  @Test
  public void test() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("recentchanges_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    RecentchangeTitles testee = new RecentchangeTitles(bot, MediaWiki.NS_MAIN);
    List<String> resultList = testee.getCopyOf(15); // query-continue is not implemented

    // THEN
    ImmutableList<String> expected = ImmutableList.of("List of tallest buildings in Pakistan",
        "Grace Stoermer", "Corpus Christi Catholic College", "Submarine Squadron 4",
        "Jonathan Lee Riches");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

  @Test
  public void testOne() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("recentchanges_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    RecentchangeTitles testee = new RecentchangeTitles(bot, MediaWiki.NS_MAIN);
    List<String> resultList = testee.getCopyOf(1);

    // THEN
    ImmutableList<String> expected = ImmutableList.of("List of tallest buildings in Pakistan");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

}
