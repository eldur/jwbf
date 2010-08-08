package net.sourceforge.jwbf.mediawiki.live;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.BacklinkTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.test.ParameterizedLabel;
import net.sourceforge.jwbf.test.SimpleNameFinder;
import net.sourceforge.jwbf.test.TestNamer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author Thomas Stock
 *
 */
@RunWith(ParameterizedLabel.class)
@TestNamer(SimpleNameFinder.class)
public class BacklinkExpTest extends LiveTestFather {

  private static final String BACKLINKS = "Backlinks";
  private static final int COUNT = 60;
  private MediaWikiBot bot = null;

  @Parameters
  public static Collection<?> regExValues() {
    return Arrays.asList(new Object[][] {
        {Version.MW1_09},
        {Version.MW1_10},
        {Version.MW1_11},
        {Version.MW1_12},
        {Version.MW1_13},
        {Version.MW1_14},
        {Version.MW1_15},
        {Version.MW1_16},
    });
  }

  public BacklinkExpTest(Version v) throws Exception {
    bot = getMediaWikiBot(v, true);
    Assert.assertEquals(v, bot.getVersion());
  }

  protected static final void doPreapare(MediaWikiBot bot) throws ActionException, ProcessException {
    SimpleArticle a = new SimpleArticle();
    for (int i = 0; i <= COUNT; i++) {
      a.setTitle("Back" + i);
      if (i % 2 == 0) {
        a.setText("#redirect [[" + BACKLINKS + "]]");
      } else {
        a.setText("[[" + BACKLINKS + "]]");
      }
      bot.writeContent(a);
    }
  }
  /**
   * Setup log4j.
   *
   * @throws Exception
   *             a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
  }

  /**
   * Test backlinks.
   *
   * @throws Exception
   *             a
   */
  @Test
  public final void test() throws Exception {
    doTest(bot);
  }


  private void doTest(MediaWikiBot bot) throws Exception {
    doTest(bot, RedirectFilter.all);
  }

  private void doTest(MediaWikiBot bot, RedirectFilter rf) throws Exception {

    BacklinkTitles gbt = new BacklinkTitles(bot, BACKLINKS, rf, MediaWiki.NS_MAIN , MediaWiki.NS_CATEGORY);

    Vector<String> vx = new Vector<String>();
    Iterator<String> is = gbt.iterator();
    boolean notEnougth = true;
    int i = 0;
    while (is.hasNext()) {
      is.next();
      i++;
      if (i > COUNT) {
        notEnougth = false;
        break;
      }
    }
    if (notEnougth) {
      System.err.println(i  + " is to less (" + COUNT + ")");
      doPreapare(bot);
    }
    is = gbt.iterator();
    vx.add(is.next());
    vx.add(is.next());
    vx.add(is.next());
    is = gbt.iterator();
    i = 0;
    while (is.hasNext()) {
      String buff = is.next();
      vx.remove(buff);
      i++;
      if (i > COUNT) {
        break;
      }
    }
    Assert.assertTrue("Iterator should contain: " + vx, vx.isEmpty());
    Assert.assertTrue("Fail: " + i + " < " + COUNT, i > COUNT - 1);
  }
}
