package net.sourceforge.jwbf.core.contentRep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class ImmutableArticleTest {

  @Test
  public void testEmptyArticle() {

    // GIVEN
    SimpleArticle simple = new SimpleArticle();
    Article in = new Article(mock(MediaWikiBot.class), simple);

    // WHEN
    ImmutableArticle immutable = ImmutableArticle.copyOf(in);

    // THEN
    assertEquals("", immutable.getEditor());
    assertEquals("", immutable.getEditSummary());
    assertEquals("", immutable.getRevisionId());
    assertEquals("", immutable.getText());
    assertEquals("", immutable.getTitle());
    assertEquals(SimpleArticle.newZeroDate(), immutable.getEditTimestamp());
    assertFalse(immutable.isMinorEdit());
    assertFalse(immutable.isRedirect());
  }

  @Test
  public void testEmptySimpleArticle() {

    // GIVEN
    SimpleArticle in = new SimpleArticle();

    // WHEN
    ImmutableArticle immutable = ImmutableArticle.copyOf(in);

    // THEN
    assertEquals("", immutable.getEditor());
    assertEquals("", immutable.getEditSummary());
    assertEquals("", immutable.getRevisionId());
    assertEquals("", immutable.getText());
    assertEquals("", immutable.getTitle());
    assertEquals(SimpleArticle.newZeroDate(), immutable.getEditTimestamp());
    assertFalse(immutable.isMinorEdit());
    assertFalse(immutable.isRedirect());
  }

  @Test
  public void testSimpleArticle() {

    // GIVEN
    DateTime now = DateTime.now();
    Date nowDate = now.toDate();
    long time = now.getMillis();
    SimpleArticle in = new SimpleArticle();
    in.setEditor("editor");
    in.setEditSummary("summary");
    in.setRevisionId("123");
    in.setText("#redirect a");
    in.setTitle("title");
    in.setEditTimestamp(nowDate);
    in.setMinorEdit(true);

    // WHEN
    ImmutableArticle immutable = ImmutableArticle.copyOf(in);

    // THEN
    nowDate.setTime(SimpleArticle.newZeroDate().getTime());
    assertEquals("editor", immutable.getEditor());
    assertEquals("summary", immutable.getEditSummary());
    assertEquals("123", immutable.getRevisionId());
    assertEquals("#redirect a", immutable.getText());
    assertEquals("title", immutable.getTitle());
    assertEquals(time, immutable.getEditTimestamp().getTime());
    assertTrue(immutable.isMinorEdit());
    assertTrue(immutable.isRedirect());
  }
}
