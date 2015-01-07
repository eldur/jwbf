package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

public class WatchListTest {

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
