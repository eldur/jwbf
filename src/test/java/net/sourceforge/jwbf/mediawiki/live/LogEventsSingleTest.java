package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.mediawiki.actions.queries.LogEvents;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.auto.LogEventsTest;

import org.junit.Test;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class LogEventsSingleTest extends AbstractMediaWikiBotTest {

  /**
   * Test category read. Test category must have more then 50 members.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = ActionException.class)
  public final void logEventsPerformManual() throws Exception {

    String liveUrl = "http://de.wikipedia.org/w/index.php";
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    LogEvents le = new LogEvents(bot, LogEvents.DELETE);
    bot.performAction(le);
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void logEventsWikipediaDe() throws Exception {
    String liveUrl = "http://de.wikipedia.org/w/index.php";
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    LogEventsTest.doTest(bot, false, LogEvents.DELETE);
  }

}
