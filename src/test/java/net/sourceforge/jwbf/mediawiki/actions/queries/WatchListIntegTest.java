package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.ApiMatcherBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MocoIntegTest;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Ignore;
import org.junit.Test;

public class WatchListIntegTest extends AbstractIntegTest {

  RequestMatcher watchlist = ApiMatcherBuilder.of() //
      .param("action", "query") //
      .param("format", "json") //
      .paramNewContinue(MediaWiki.Version.MW1_24) //
      .param("list", "watchlist") //
      .param("wllimit", "max") //
      .param("wlnamespace", "0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15") //
      .param("wlshow", "bot|anon|minor") //
      .build();

  RequestMatcher loginSuccess = ApiMatcherBuilder.of() //
      .param("action", "login") //
      .param("format", "json") //
      .build();

  @Test
  @Ignore // TODO remove
  public void test() {
    // GIVEN
    server.request(loginSuccess).response(TestHelper.anyWikiResponse("login_valid.json"));
    MocoIntegTest.applySiteinfoXmlToServer(server, MediaWiki.Version.MW1_24, this.getClass());
    server.request(watchlist).response(TestHelper.anyWikiResponse("watchlist.json"));
    MediaWikiBot bot = new MediaWikiBot(host());
    bot.login("Hunsu", "password");

    // WHEN
    WatchList testee = WatchList.from(bot) //
        .withProperties(WatchList.WatchListProperties.values()) //
        .owner("Hunsu", "notoken") //
        .build();
    ImmutableList<WatchResponse> resultList = testee.getCopyOf(3);

    // THEN
    ImmutableList<WatchResponse> expected = ImmutableList.of(); // TODO fill this list
    GAssert.assertEquals(expected, resultList);

  }
}
