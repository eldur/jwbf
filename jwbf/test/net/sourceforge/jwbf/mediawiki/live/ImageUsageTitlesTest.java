package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageUsageTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.BeforeClass;
import org.junit.Test;
/**
 *
 * @author Thomas Stock
 *
 */
public class ImageUsageTitlesTest extends LiveTestFather {


  private MediaWikiBot bot = null;
  private static final int limit = 55;

  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(ImageUsageTitles.class);
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x09() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
    test(bot);

  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x10() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
    test(bot);

  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
    test(bot);

  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
    test(bot);

  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x13() throws Exception {

    bot = getMediaWikiBot(Version.MW1_13, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
    test(bot);

  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x14() throws Exception {

    bot = getMediaWikiBot(Version.MW1_14, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
    test(bot);

  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x15() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
    test(bot);

  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void imageUsageMW1x16() throws Exception {

    bot = getMediaWikiBot(Version.MW1_16, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
    test(bot);

  }

  private void test(MediaWikiBot bot2) throws Exception {
    ImageUsageTitles il = new ImageUsageTitles(bot, "Image:" + getValue("filename"), MediaWiki.NS_ALL);

    boolean notFound = true;
    int x = 0;
    for (String string : il) {
      System.out.println(string);
      x++;
      if (x >= limit) {
        notFound = false;
        break;
      }
    }
    if (notFound) {
      prepare(bot2);
    }
    x = 0;
    for (String string : il) {
      System.out.println(string);
      x++;
      if (x >= limit) {
        break;
      }
    }

    if (x < limit) {
      fail("limit" + x);
    }
    registerTestedVersion(ImageUsageTitles.class, bot.getVersion());

  }
  private void prepare(MediaWikiBot bot2) throws Exception {

    String name = "";
    for (int i = 0; i < limit; i++) {
      name = "TitleWithImg" + i;
      Article a = new Article(bot2, name);
      a.setText("Hello [[Image:" + getValue("filename") + "]] a image " + getRandom(10));
      a.save();
    }

  }
}
