package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.assumeReachable;
import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.TestHelper.getRandomAlph;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.LogEvents;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class LogEventsTest extends AbstractMediaWikiBotTest {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      LogEvents.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  private static final int LIMIT = 55;

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
    doTest(bot, false, LogEvents.DELETE);
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void logEventsMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void logEventsMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_16.equals(bot.getVersion()));
  }

  private void doPrepare(MediaWikiBot bot) {
    for (int i = 0; i <= LIMIT; i++) {
      String title = getRandomAlph(6);
      Article a = new Article(bot, title);
      a.setText(getRandom(5));
      a.save();
      assertTrue("content shoul be", a.getText().length() > 0);
      a.delete();
    }
  }

  private void doTest(MediaWikiBot bot, boolean isDemo, String type)
      throws Exception {
    LogEvents le = new LogEvents(bot, type);

    int i = 0;
    boolean notEnough = true;
    for (@SuppressWarnings("unused")
    LogItem logItem : le) {
      i++;
      if (i > LIMIT) {
        notEnough = false;
        break;
      }
    }
    if (notEnough && isDemo) {
      doPrepare(bot);
    }

    for (LogItem logItem : le) {
      System.out.print(logItem.getTitle() + " ");
      i++;
      if (i > LIMIT) {
        break;
      }
    }
    assertTrue("should be greater then 50 but is " + i, i > LIMIT);
  }
}
