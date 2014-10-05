package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogEventsTest {

  @Mock
  MediaWikiBot bot;

  @Test
  public void testInit() {
    new LogEvents(bot, "test");
  }

  @Test
  public void testInitLimt_negative() {
    try {
      new LogEvents(bot, -10, LogEvents.BLOCK);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("limit must be > 0, but was -10", e.getMessage());
    }
  }

  @Test
  public void testInitLimt_zero() {
    try {
      new LogEvents(bot, 0, LogEvents.IMPORT);
      fail();
    } catch (IllegalArgumentException e) {
      assertEquals("limit must be > 0, but was 0", e.getMessage());
    }
  }
}
