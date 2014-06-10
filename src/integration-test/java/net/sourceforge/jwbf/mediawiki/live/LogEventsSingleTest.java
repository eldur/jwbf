package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.actions.queries.LogEvents;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.auto.LogEventsTest;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public class LogEventsSingleTest extends AbstractMediaWikiBotTest {

  @Test
  public final void logEventsPerformManual() {
    String liveUrl = getWikipediaDeUrl();
    bot = new MediaWikiBot(liveUrl);
    LogEvents le = new LogEvents(bot, LogEvents.DELETE);
    try {
      bot.getPerformedAction(le);
      fail();
    } catch (ActionException e) {
      assertEquals("this is a selfexcecuting action, please do not perform this action manually", e.getMessage());
    }
  }

  @Test
  public final void logEventsWikipediaDe() {
    String liveUrl = getWikipediaDeUrl();
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    LogEventsTest.doTest(bot, false, LogEvents.DELETE);
  }

}
