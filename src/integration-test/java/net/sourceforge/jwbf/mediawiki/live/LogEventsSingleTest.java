package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.actions.queries.LogEvents;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.auto.LogEventsTest;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public class LogEventsSingleTest extends AbstractMediaWikiBotTest {

  /**
   * Test category read. Test category must have more then 50 members.
   */
  @Test(expected = ActionException.class)
  public final void logEventsPerformManual() {

    String liveUrl = getWikipediaDeUrl();
    bot = new MediaWikiBot(liveUrl);
    LogEvents le = new LogEvents(bot, LogEvents.DELETE);
    bot.performAction(le);
  }

  @Test
  public final void logEventsWikipediaDe() {
    String liveUrl = getWikipediaDeUrl();
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    LogEventsTest.doTest(bot, false, LogEvents.DELETE);
  }

}
