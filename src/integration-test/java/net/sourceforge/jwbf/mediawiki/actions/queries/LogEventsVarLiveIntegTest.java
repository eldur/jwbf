package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.AbstractMediaWikiBotTest;
import org.junit.Test;

/**
 * @author Thomas Stock
 */
public class LogEventsVarLiveIntegTest extends AbstractMediaWikiBotTest {

  @Test
  public final void logEventsWikipediaDe() {
    String liveUrl = getWikipediaDeUrl();
    assumeReachable(liveUrl);
    bot = new MediaWikiBot(liveUrl);
    LogEventsLiveIntegTest.doTest(bot, false, LogEvents.DELETE);
  }

}
