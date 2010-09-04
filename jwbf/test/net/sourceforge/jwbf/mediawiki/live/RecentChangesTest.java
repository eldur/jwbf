/**
 *
 */
package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.RecentchangeTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas Stock
 *
 */
public class RecentChangesTest extends LiveTestFather {
  private MediaWikiBot bot = null;
  private static final int COUNT = 13;
  private static final int LIMIT = COUNT * 2;
  /**
   *
   * @throws Exception a
   */
  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(RecentchangeTitles.class);
  }


  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x09() throws Exception {
    bot = getMediaWikiBot(Version.MW1_09, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_09.equals(bot.getVersion()));
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x10() throws Exception {

    bot = getMediaWikiBot(Version.MW1_10, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_10.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x11() throws Exception {
    bot = getMediaWikiBot(Version.MW1_11, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_11.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x12() throws Exception {
    bot = getMediaWikiBot(Version.MW1_12, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_12.equals(bot.getVersion()));
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_13.equals(bot.getVersion()));
  }

  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_14.equals(bot.getVersion()));
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
  }
  /**
   * Test.
   * @throws Exception a
   */
  @Test
  public final void recentChangesWikiMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doRegularTest(bot);
    doSpecialCharTest(bot);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
  }

  private void prepareWiki(MediaWikiBot bot) throws ActionException,
  ProcessException {
    SimpleArticle a = new SimpleArticle();
    for (int i = 0; i < 5 + 1; i++) {
      String label = getRandom(10);
      for (char c : MediaWikiBot.INVALID_LABEL_CHARS) {
        label = label.replace(c + "", "");
      }
      a.setTitle(label);
      a.setText(getRandom(255));
      bot.writeContent(a);
    }

  }
  private void doSpecialCharTest(MediaWikiBot bot) throws ActionException,
  ProcessException {
    Article sa;
    String testText = getRandom(255);


    Collection<String> specialChars = getSpecialChars();
    try {
      for (String title : specialChars) {
        sa = new Article(bot, title);
        sa.setText(testText);
        sa.save();
      }
    } catch (ActionException e) {
      boolean found = false;
      for (char ch : MediaWikiBot.INVALID_LABEL_CHARS) {
        if (e.getMessage().contains(ch + "")) {
          found = true;
          break;
        }
      }
      assertTrue("should be a know invalid char",  found);
    }

    RecentchangeTitles rc = new RecentchangeTitles(bot);

    Iterator<String> is = rc.iterator();
    int i = 0;
    int size = specialChars.size();

    while (is.hasNext() && i < (size * 1.2)) {
      String nx = is.next();
      System.err.println("rm " + nx);
      specialChars.remove(nx);
      i++;
    }
    for (char c : MediaWikiBot.INVALID_LABEL_CHARS) {
      specialChars.remove(c + "");
    }

    assertTrue("tc sould be empty but is: " + specialChars, specialChars.isEmpty());

  }

  private void doRegularTest(MediaWikiBot bot) throws ActionException,
  ProcessException {
    prepareWiki(bot);
    RecentchangeTitles rc = new RecentchangeTitles(bot);

    Iterator<String> is = rc.iterator();
    int i = 0;



    Vector<Integer> vi = new Vector<Integer>();
    try {
      is = rc.iterator();

      i = 0;
      vi.clear();
      for (int j = 0; j < COUNT; j++) {

        vi.add(j);

      }

      while (is.hasNext()) {
        String s = is.next();
        int x = Integer.parseInt(s.split(" ")[1]);
        // System.out.println(vi);
        vi.remove(Integer.valueOf(x));
        i++;
        if (i > LIMIT || vi.isEmpty()) {
          break;
        }
      }
      if (!vi.isEmpty()) {
        throw new Exception();
      }
    } catch (Exception e) {
      change(bot);
      is = rc.iterator();

      i = 0;
      vi.clear();
      for (int j = 0; j < COUNT; j++) {

        vi.add(j);

      }

      while (is.hasNext()) {
        String s = is.next();
        String [] digets = s.split(" ");
        if (digets != null && digets.length > 0) {
          int x = Integer.parseInt(digets[1]);
          //				System.err.println("rm " + s + " ~= " + x);
          vi.remove(Integer.valueOf(x));
        }
        i++;
        if (i > LIMIT || vi.isEmpty()) {
          break;
        }
      }
    }
    assertTrue("shuld be empty but is : " + vi, vi.isEmpty());
    assertTrue("i is: " + i, i > COUNT - 1);
    registerTestedVersion(RecentchangeTitles.class, bot.getVersion());
  }
  private void change(MediaWikiBot bot) throws ActionException, ProcessException {
    SimpleArticle a = new SimpleArticle();
    for (int i = 0; i < COUNT + 1; i++) {
      a.setTitle("%Change " + i);
      a.setText(getRandom(255));
      bot.writeContent(a);
    }
  }
}
