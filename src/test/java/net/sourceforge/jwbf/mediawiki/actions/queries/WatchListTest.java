package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Iterator;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.mediawiki.actions.queries.WatchList.WatchListProperties;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;

public class WatchListTest extends AbstractIntegTest {
  @Test
  public void test() {
    /*MediaWikiBot bot = mock(MediaWikiBot.class);
    when(bot.isLoggedIn()).thenReturn(true);*/

    MediaWikiBot bot = new MediaWikiBot("https://fr.wikipedia.org/w/");
    bot.login("Hunsu", "password");
    WatchList testee =
        WatchList.from(bot).withProperties(WatchListProperties.values()).owner("Hunsu", "notoken")
            .build();
    Iterator<WatchResponse> iterator = testee.iterator();
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }

}
