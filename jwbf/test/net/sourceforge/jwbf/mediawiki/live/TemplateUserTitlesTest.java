package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Vector;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.TemplateUserTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class TemplateUserTitlesTest extends LiveTestFather {


  private MediaWikiBot bot = null;
  private static final String TESTPATTERNNAME = "Template:ATesT";
  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(TemplateUserTitles.class);
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x09() throws Exception {

    bot = getMediaWikiBot(Version.MW1_09, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x10() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x11() throws Exception {

    bot = getMediaWikiBot(Version.MW1_11, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x12() throws Exception {

    bot = getMediaWikiBot(Version.MW1_12, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void templateUserWikiMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doRegularTest(bot);

    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
  }

  private void doRegularTest(MediaWikiBot bot) throws JwbfException {
    TemplateUserTitles a = new TemplateUserTitles(bot, TESTPATTERNNAME, MediaWiki.NS_ALL);

    int i = 0;
    Collection<String> titles = new Vector<String>();

    for (int j = 0; j < 55; j++) {
      titles.add("Patx" + j);
    }

    for (String pageTitle : a) {
      pageTitle += "";
      i++;
    }
    if (i < 50) {
      prepare(bot, titles);
    }

    for (String pageTitle : a) {
      titles.remove(pageTitle);
      System.out.println(titles);
      i++;
    }
    if (i < 50) {
      fail("to less " + i);
    }
    assertTrue("title collection should be empty", titles.isEmpty());



    Article template = new Article(bot, TESTPATTERNNAME);
    assertEquals(TESTPATTERNNAME + " content ", "a test", template.getText());
    registerTestedVersion(TemplateUserTitles.class, bot.getVersion());
  }

  private void prepare(MediaWikiBot bot, Collection<String> titles) throws JwbfException {
    Article template = new Article(bot, TESTPATTERNNAME);
    template.setText("a test");
    template.save();

    for (String title : titles) {
      Article a = new Article(bot, title);
      a.setText(getRandom(1) + " {{" + TESTPATTERNNAME + "}}");
      a.save();
    }

  }
}
