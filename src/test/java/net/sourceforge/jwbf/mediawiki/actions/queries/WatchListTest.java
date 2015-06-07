package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.mediawiki.actions.queries.WatchList.WatchListProperties;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Ignore;
import org.junit.Test;

public class WatchListTest extends AbstractIntegTest {
  @Test
  @Ignore
  // TODO @Hunsu move to integration-test package
  public void test() {
    // GIVEN

    /*MediaWikiBot bot = mock(MediaWikiBot.class);
    when(bot.isLoggedIn()).thenReturn(true);*/

    MediaWikiBot bot = new MediaWikiBot("https://fr.wikipedia.org/w/");
    bot.login("Hunsu", "password");
    WatchList testee = WatchList.from(bot) //
        .withProperties(WatchListProperties.values()) //
        .owner("Hunsu", "notoken") //
        .build();

    // WHEN
    Iterator<WatchResponse> iterator = testee.iterator();

    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }

}
