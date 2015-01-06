package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Iterator;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.mediawiki.actions.queries.WatchList.WatchListProperties;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

public class WatchListTest extends AbstractIntegTest {

  // TODO more tests :-)
  @Ignore
  @Test
  public void test() {
    /*MediaWikiBot bot = mock(MediaWikiBot.class);
    when(bot.isLoggedIn()).thenReturn(true);*/

    MediaWikiBot bot = new MediaWikiBot("https://fr.wikipedia.org/w/");
    bot.login("Hunsu", "password");
    WatchList testee = WatchList.from(bot) //
        .withProperties(WatchListProperties.values()) //
        .owner("Hunsu", "notoken") //
        .build();
    Iterator<WatchResponse> iterator = testee.iterator();
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }

  @Test
  public void testTimeFormatting() {
    // GIVEN
    Date date = DateTime.parse("2008-03-04T17:01:48+0100").toDate();

    // WHEN
    String formattedDate = WatchList.formatDate(date);

    // THEN
    assertEquals("2008-03-04T16:01:48Z", formattedDate);
  }

}
