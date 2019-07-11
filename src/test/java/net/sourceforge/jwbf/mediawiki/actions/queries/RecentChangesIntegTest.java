package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.RecentChange;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static net.sourceforge.jwbf.mediawiki.contentRep.RecentChange.ChangeType;
import static org.junit.Assert.assertEquals;

public class RecentChangesIntegTest extends AbstractIntegTest {

  private RequestMatcher embeddedinTwo =
      ApiMatcherBuilder.of() //
          .param("action", "query") //
          .param("format", "xml") //
          .param("list", "recentchanges") //
          .param("rclimit", "50") //
          .param("rcnamespace", "0") //
          .param("rcprop", "user|userid|comment|flags|timestamp|title|ids|sizes|flags") //
          .build();

  @Test
  public void test() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("recentchanges_full_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    RecentChanges testee = new RecentChanges(bot, MediaWiki.NS_MAIN);
    List<RecentChange> resultList = testee.getCopyOf(15); // query-continue is not implemented

    // THEN
    ImmutableList<RecentChange> expected =
        ImmutableList.of(
            new RecentChange(ChangeType.EDIT, 3, "User talk:Troll", 60008248, 883961999,
                883961612, 1131171831, "Thanos", 31143093, 2623, 3194,
                toDate("2019-02-18T18:26:42Z"), "Things and stuff"),
            new RecentChange(ChangeType.CATEGORIZE, 14, "Category:Items", 176846, 883961997,
                883961959, 1131171832, "Thanos", 31143093, 0, 0,
                toDate("2019-02-18T18:26:41Z"), "[[:Infinity stone]] added to category"),
            new RecentChange(ChangeType.EDIT, 0, "Iron Man", 176846, 883961997, 883961959,
                1131171828, "Thanos", 31143093, 25012, 25007, toDate("2019-02-18T18:26:41Z"),
                "Reverted edits by [[Special:Contributions/Troll|Troll]]"),
            new RecentChange(ChangeType.CATEGORIZE, 14, "Category:Characters", 60008350,
                883961992, 0, 1131171823, "Thor", 23052847, 0, 0, toDate("2019-02-18T18:26:39Z"),
                "[[:File:Myself.png]] added to category"),
            new RecentChange(ChangeType.LOG, 6, "File:Myself.png", 60008350, 883961992, 0,
                1131171820, "Thor", 23052847, 0, 0, toDate("2019-02-18T18:26:39Z"),
                "Uploading a photo of myself"),
            new RecentChange(ChangeType.NEW, 0, "Guardians", 60008351, 883961993, 0,
                1131171819, "Rocket", 7611264, 0, 299, toDate("2019-02-18T18:26:39Z"),
                "Redirecting to [[:Guardians of the Galaxy]]")
        );
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());
  }

  @Test
  public void testOne() {

    // GIVEN
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("recentchanges_full_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    RecentChanges testee = new RecentChanges(bot, MediaWiki.NS_MAIN);
    List<RecentChange> resultList = testee.getCopyOf(1);

    // THEN
    ImmutableList<RecentChange> expected = ImmutableList.of(new RecentChange(ChangeType.EDIT, 3,
        "User talk:Troll", 60008248, 883961999, 883961612, 1131171831,
        "Thanos", 31143093, 2623, 3194, toDate("2019-02-18T18:26:42Z"), "Things and stuff"));
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());
  }

  private static Date toDate(String date) {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    try {
      return dateFormat.parse(date);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
