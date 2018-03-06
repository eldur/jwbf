package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.TestHelper.getRandomAlph;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.BotFactory.CacheActionClient;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.actions.login.PostLogin;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class ArticleIT {

  private static final Logger log = LoggerFactory.getLogger(ArticleIT.class);

  private Collection<MediaWikiBot> getTestBots() {
    Collection<MediaWikiBot> bots = Lists.newArrayList();
    bots.add(getMediaWikiBot(Version.MW1_19, true));
    bots.add(getMediaWikiBot(Version.MW1_23, true));
    return bots;
  }

  @Test
  public void testErrorEdit() {
    String title = "z error " + getRandomAlph(3);
    MediaWikiBot bot = getMediaWikiBot(Version.getLatest(), true);
    Article a = new Article(bot, title);
    try {
      a.setText(getRandom(42));
      a.save();
    } finally {
      a.delete();
    }
  }

  @Test
  public final void readWriteDelete() {

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
        assertEquals("text compair fails ", a.getText(), b.getText());
        // ^^ forces bot to load this from wiki
        assertEquals(user, b.getEditor());
        if (i > 1) {
          assertEquals(editSum, b.getEditSummary());
        }
        assertEquals(saveDate.getTime(), b.getEditTimestamp().getTime(), 5000);
        // ^^ max. 5 seconds delta

      }
      a.delete(); // clean up
    }
  }

  @Test
  @Ignore("check later")
  public final void meta() {

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
      String revIdApp = b.getRevisionId();
      Date dateB = b.getEditTimestamp();
      assertNotSame("change expected " + bot.getWikiType(), revIdA, revIdApp);
      assertNotSame("change expected " + bot.getWikiType() + " " + dateA, dateA, dateB);
      assertEquals("minor edit @ " + bot.getWikiType(), a.isMinorEdit(), b.isMinorEdit());

      try {
        a.delete(); // clean up
      } catch (VersionException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public final void simpleArticleTest() {
    LiveTestFather.skipIfIsNoIntegTest();
    Injector injector = BotFactory.getBotInjector(Version.getLatest(), true);
    MediaWikiBot b = injector.getInstance(MediaWikiBot.class);

    SimpleArticle sa = b.readData("Main Page");
    sa.getText();
    sa.getText();

    CacheActionClient actionClient = injector.getInstance(CacheActionClient.class);
    verify(actionClient, times(1)).performAction(Mockito.isA(PostLogin.class));
    verify(actionClient, times(1)).performAction(Mockito.isA(GetVersion.class));
    verify(actionClient, times(1)).performAction(Mockito.isA(GetRevision.class));
    Mockito.reset(actionClient);
    try {
      SimpleArticle sa2 = new SimpleArticle(b.getArticle("Main Page"));
      sa2.getText();
      sa2.getText();
      // very expensive (for documentation)
      verify(actionClient, times(7)).performAction(Mockito.isA(GetRevision.class));
      fail();
    } catch (IllegalArgumentException iae) {
      assertEquals(
          "do not convert an net.sourceforge.jwbf.core.contentRep.Article"
              + //
              " to a net.sourceforge.jwbf.core.contentRep.SimpleArticle, "
              + //
              "because its very expensive",
          iae.getMessage());
    }
  }

  @Test
  public final void articleTest() {
    LiveTestFather.skipIfIsNoIntegTest();
    Injector injector = BotFactory.getBotInjector(Version.getLatest(), true);
    MediaWikiBot b = injector.getInstance(MediaWikiBot.class);

    Article sa = b.getArticle("Main Page");
    sa.getText();
    sa.getText();
    sa.getTitle();
    sa.getTitle();

    CacheActionClient actionClient = injector.getInstance(CacheActionClient.class);
    verify(actionClient, times(1)).performAction(Mockito.isA(PostLogin.class));
    verify(actionClient, times(1)).performAction(Mockito.isA(GetVersion.class));
    verify(actionClient, times(2)).performAction(Mockito.isA(GetRevision.class));
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
    assertFalse("text should be differ:\n" + aaText + "\n" + aText, aaText.equals(aText));
    assertTrue(
        "dif rev ID, both: " + thirdEdit,
        Integer.parseInt(firstEdit) != Integer.parseInt(thirdEdit));

    log.debug("--> end article test");
  }
}
