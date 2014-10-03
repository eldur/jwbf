package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;

public class RecentchangeTitlesIntegTest extends AbstractIntegTest {

  RequestMatcher embeddedinTwo = ApiMatcherBuilder.of() //
      .param("action", "query") //
      .param("format", "xml") //
      .param("list", "recentchanges") //
      .param("rclimit", "50") //
      .param("rcnamespace", "0") //
      .build();

  @Test
  public void test() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("recentchanges_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    RecentchangeTitles testee = new RecentchangeTitles(bot, MediaWiki.NS_MAIN);
    List<String> resultList = testee.getCopyOf(15); // query-continue is not implemented

    // THEN
    ImmutableList<String> expected =
        ImmutableList.of("List of tallest buildings in Pakistan", "Grace Stoermer",
            "Corpus Christi Catholic College", "Submarine Squadron 4", "Jonathan Lee Riches");
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
