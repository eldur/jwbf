package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.TestHelper.getRandomAlph;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Test;

@Slf4j
public class ArticleTest {

  private Collection<MediaWikiBot> getTestBots() {
    Collection<MediaWikiBot> bots = new Vector<MediaWikiBot>();
    bots.add(getMediaWikiBot(Version.MW1_16, true));
    bots.add(getMediaWikiBot(Version.MW1_15, true));
    return bots;
  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void readWriteDelete() throws Exception {

    Collection<MediaWikiBot> bots = getTestBots();

    for (MediaWikiBot bot : bots) {

      String title = "z" + getRandomAlph(3); // create random title
      String user = bot.getUserinfo().getUsername();
      Article a = new Article(bot, title);
      for (int i = 0; i <= 2; i++) {
        String editSum = getRandomAlph(6); // create random edit sum
        a = new Article(bot, title); // create new article with given title
        a.setText(getRandom(42)); // set random text
        Date saveDate = LiveTestFather.getCurrentUTC();
        a.save(editSum); // save article a with given comment

        Article b = new Article(bot, title); // create new article b
        assertEquals(a.getTitle(), b.getTitle()); // compare title, must work --
                                                  // see constructor
        assertEquals("text compair fails ", a.getText(), b.getText()); // force
                                                                       // bot to
                                                                       // load
                                                                       // this
                                                                       // from
                                                                       // wiki
        assertEquals(user, b.getEditor());
        if (i > 1) {
          assertEquals(editSum, b.getEditSummary());
        }
        assertEquals(saveDate.getTime(), b.getEditTimestamp().getTime(), 5000); // max.
                                                                                // 5
                                                                                // seconds
                                                                                // delta

      }
      try {
        a.delete(); // clean up
      } catch (VersionException e) {
        e.printStackTrace();
      }

    }

  }

  /**
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void meta() throws Exception {

    Collection<MediaWikiBot> bots = getTestBots();

    for (MediaWikiBot bot : bots) {

      String title = "z" + getRandomAlph(6);
      String user = bot.getUserinfo().getUsername();
      String editSum = getRandomAlph(6);
      Article a = new Article(bot, title);
      a.setText(getRandom(42));
      a.setMinorEdit(false);

      Date saveDate = LiveTestFather.getCurrentUTC();
      a.save(editSum); // save article a
      String revIdA = a.getRevisionId();
      Date dateA = a.getEditTimestamp();
      Article b = new Article(bot, title);
      assertEquals(a.getTitle(), b.getTitle());
      assertEquals(a.getText(), b.getText());
      assertEquals(a.isMinorEdit(), b.isMinorEdit()); // because false is
                                                      // default value
      assertEquals(user, b.getEditor());
      assertEquals(editSum, b.getEditSummary());
      assertEquals(saveDate.getTime(), b.getEditTimestamp().getTime(), 5000); // max.
                                                                              // 5
                                                                              // seconds
                                                                              // delta

      a.setMinorEdit(true);
      a.save(); // do nothing because no content change

      String revIdAp = a.getRevisionId();
      assertEquals("no change " + bot.getWikiType(), revIdA, revIdAp);
      a.addText(getRandom(48));
      a.save();
      String revIdApp = a.getRevisionId();
      Date dateB = a.getEditTimestamp();
      assertNotSame("change expected " + bot.getWikiType(), revIdA, revIdApp);
      assertNotSame("change expected " + bot.getWikiType() + " " + dateA,
          dateA, dateB);
      assertEquals("minor edit @ " + bot.getWikiType(), a.isMinorEdit(),
          b.isMinorEdit());

      try {
        a.delete(); // clean up
      } catch (VersionException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public final void simpleArticleTest() {
    MediaWikiBot b = getMediaWikiBot(Version.getLatest(), true);
    SimpleArticle sa = b.readData("Main Page");
    System.out.println(sa.getText());
  }

  @Test
  public final void articleReadFreqTest() throws Exception {
    log.debug("-- > Begin articleTest");
    // get a MediaWikiBot
    MediaWikiBot bot = getMediaWikiBot(Version.getLatest(), true);
    // create new article on this wikibot
    Article a = new Article(bot, "Test");
    a.setText(getRandom(7));
    final String aText = a.getText();
    log.debug("pre save");
    // save content
    a.save();
    log.debug("after save");
    assertFalse("shoud be no minor edit", a.isMinorEdit());
    final String firstEdit = a.getRevisionId();
    a.setMinorEdit(true);
    a.save("comment");
    final String secondEdit = a.getRevisionId();
    assertEquals("same rev ID", firstEdit, secondEdit);
    a.addText(getRandom(16));
    final String aaText = a.getText();
    a.save();
    final String thirdEdit = a.getRevisionId();
    assertTrue(a.isMinorEdit());
    assertFalse("text should be differ:\n" + aaText + "\n" + aText,
        aaText.equals(aText));
    assertTrue("dif rev ID, both: " + thirdEdit,
        Integer.parseInt(firstEdit) != Integer.parseInt(thirdEdit));

    log.debug("--> end article test");
  }

}
