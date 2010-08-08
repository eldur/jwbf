/**
 * 
 */
package net.sourceforge.jwbf.mediawiki.live;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostDelete;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas
 *
 */
public class DeleteTest extends LiveTestFather {
  private MediaWikiBot bot = null;
  private static final int COUNT = 1;

  /***
   * 
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(PostDelete.class);


  }
  private void prepare(MediaWikiBot bot) throws Exception {
    SimpleArticle a = new SimpleArticle();

    for (int i = 0; i < COUNT; i++) {
      a.setTitle("Delete " + i);
      a.setText(getRandom(23));
      bot.writeContent(a);
    }
  }

  private void delete(MediaWikiBot bot) throws ActionException, ProcessException {

    for (int i = 0; i < COUNT; i++) {
      bot.postDelete("Delete " + i);
    }
  }

  private void test(MediaWikiBot bot) throws ActionException, ProcessException {

    for (int i = 0; i < COUNT; i++) {
      ContentAccessable ca = bot.readContent("Delete " + i);

      Assert.assertTrue("textlength of Delete "
          + i + " is greater then 0 (" + ca.getText().length()
          + ")", ca.getText().length() == 0);
      registerTestedVersion(PostDelete.class, bot.getVersion());


    }
  }


  /**
   * Test.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void deleteWikiMW1x09Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
    registerUnTestedVersion(PostDelete.class, bot.getVersion());
    prepare(bot);
    delete(bot);
    test(bot);


  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void deleteWikiMW1x10Fail() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
    registerUnTestedVersion(PostDelete.class, bot.getVersion());

    prepare(bot);
    delete(bot);
    test(bot);


  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test(expected = VersionException.class)
  public final void deleteWikiMW1x11Fail() throws Exception {
    bot = getMediaWikiBot(Version.MW1_11, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
    registerUnTestedVersion(PostDelete.class, bot.getVersion());

    prepare(bot);
    delete(bot);
    test(bot);


  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void deleteWikiMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);


  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void deleteWikiMW1x13() throws Exception {

    bot = getMediaWikiBot(Version.MW1_13, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);


  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void deleteWikiMW1x14() throws Exception {

    bot = getMediaWikiBot(Version.MW1_14, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void deleteWikiMW1x15() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void deleteWikiMW1x16() throws Exception {

    bot = getMediaWikiBot(Version.MW1_16, true);
    Assert.assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }
}
