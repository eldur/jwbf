/**
 * 
 */
package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostDelete;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Thomas
 * 
 */
public class DeleteTest extends LiveTestFather {
  private static final String DELETE_PREFIX = "Delete";
  private MediaWikiBot bot = null;
  private static final int COUNT = 1;

  /***
   * 
   * @throws Exception
   *           a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(PostDelete.class);

  }

  private void prepare(MediaWikiBot bot) throws Exception {
    SimpleArticle a = new SimpleArticle();

    for (int i = 0; i < COUNT; i++) {
      a.setTitle(DELETE_PREFIX + i);
      a.setText(getRandom(23));
      bot.writeContent(a);
    }
  }

  private void delete(MediaWikiBot bot) throws ActionException,
  ProcessException {

    for (int i = 0; i < COUNT; i++) {
      bot.postDelete(DELETE_PREFIX + i);
    }
  }

  private void test(MediaWikiBot bot) throws ActionException, ProcessException {

    for (int i = 0; i < COUNT; i++) {
      ContentAccessable ca = bot.readContent(DELETE_PREFIX + i);

      assertTrue("textlength of Delete " + i + " is greater then 0 ("
          + ca.getText().length() + ")", ca.getText().length() == 0);
      registerTestedVersion(PostDelete.class, bot.getVersion());

    }
  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = VersionException.class)
  public final void deleteWikiMW1x09Fail() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_09, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_09.equals(bot.getVersion()));
    registerUnTestedVersion(PostDelete.class, bot.getVersion());
    prepare(bot);
    delete(bot);
    test(bot);

  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = VersionException.class)
  public final void deleteWikiMW1x10Fail() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_10, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_10.equals(bot.getVersion()));
    registerUnTestedVersion(PostDelete.class, bot.getVersion());

    prepare(bot);
    delete(bot);
    test(bot);

  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test(expected = VersionException.class)
  public final void deleteWikiMW1x11Fail() throws Exception {
    bot = BotFactory.getMediaWikiBot(Version.MW1_11, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_11.equals(bot.getVersion()));
    registerUnTestedVersion(PostDelete.class, bot.getVersion());

    prepare(bot);
    delete(bot);
    test(bot);

  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x12() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_12, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_12.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);

  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x13() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_13, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_13.equals(bot.getVersion()));
    assertTrue(bot.isLoggedIn());
    prepare(bot);
    delete(bot);
    test(bot);

  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x14() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_14, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_14.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x15() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_15, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_15.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x16() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_16, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_16.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  @Ignore
  @Test
  public void deleteAll() throws Exception {
    Version[] all = { Version.MW1_12, Version.MW1_13, Version.MW1_14,
        Version.MW1_15, Version.MW1_16 };
    for (Version v : all) {
      MediaWikiBot bot = BotFactory.getMediaWikiBot(v, true);
      AllPageTitles aPages = new AllPageTitles(bot, MediaWiki.NS_MAIN);
      for (String string : aPages) {
        System.out.println(string);
      }
    }
  }
}
