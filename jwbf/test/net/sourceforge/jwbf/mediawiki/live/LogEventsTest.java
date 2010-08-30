package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.LogEvents;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Thomas Stock
 *
 */
public class LogEventsTest extends LiveTestFather {


  private MediaWikiBot bot = null;
  private static final int LIMIT = 55;
  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(LogEvents.class);

  }
  /**
   * Test category read. Test category must have more then 50 members.
   * @throws Exception a
   */
  @Test(expected = ActionException.class)
  public final void logEventsPerformManual() throws Exception {

    String url = "http://de.wikipedia.org/w/index.php";
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    LogEvents le = new LogEvents(bot, LogEvents.DELETE);
    bot.performAction(le);
  }
  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsWikipediaDe() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php";
    assumeReachable(url);
    bot = new MediaWikiBot(url);
    doTest(bot, false, LogEvents.DELETE);
  }

  /**
   *
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void logEventsMW1x09Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    registerUnTestedVersion(LogEvents.class, bot.getVersion());
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
  }
  /**
   *
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void logEventsMW1x10Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    registerUnTestedVersion(LogEvents.class, bot.getVersion());
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
  }

  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    doTest(bot, true, LogEvents.UPLOAD);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
  }

  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
  }
  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
  }
  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
  }

  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   *
   * @throws Exception a
   */
  @Test
  public final void logEventsMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doTest(bot, true, LogEvents.DELETE);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
  }

  private void doPrepare(MediaWikiBot bot) throws Exception {
    for (int i = 0; i <= LIMIT; i++) {
      String title = getRandomAlph(6);
      Article a = new Article(bot, title);
      a.setText(getRandom(5));
      a.save();
      assertTrue("content shoul be", a.getText().length() > 0);
      try {
        a.delete();
      } catch (VersionException e) {
        if (!MediaWiki.Version.MW1_11.equals(bot.getVersion()))
          throw e;
      }
    }
  }

  private void doTest(MediaWikiBot bot, boolean isDemo, String type) throws Exception {
    LogEvents le = new LogEvents(bot, type);

    int i = 0;
    boolean notEnough = true;
    for (@SuppressWarnings("unused") LogItem logItem : le) {
      i++;
      if (i > LIMIT) {
        notEnough = false;
        break;
      }
    }
    if (notEnough && isDemo && !MediaWiki.Version.MW1_11.equals(bot.getVersion())) {
      doPrepare(bot);
    }

    for (LogItem logItem : le) {
      System.out.print(logItem.getTitle() + " ");
      i++;
      if (i > LIMIT) {
        break;
      }
    }
    if (MediaWiki.Version.MW1_11.equals(bot.getVersion())) {
      registerTestedVersion(LogEvents.class, bot.getVersion());
      Assume.assumeTrue(i > LIMIT);
    } else
      assertTrue("should be greater then 50 but is " + i, i > LIMIT);
    registerTestedVersion(LogEvents.class, bot.getVersion());
  }
}
