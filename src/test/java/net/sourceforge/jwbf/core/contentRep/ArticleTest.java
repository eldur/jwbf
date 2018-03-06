package net.sourceforge.jwbf.core.contentRep;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.jwbf.core.bots.WikiBot;

public class ArticleTest {

  @Test
  public void testInit_with_title() {
    // GIVEN
    WikiBot bot = mock(WikiBot.class);
    String title = "test title";
    SimpleArticle testArticle = new SimpleArticle(title);
    testArticle.setText("test text");
    when(bot.readData(title)).thenReturn(testArticle);

    // WHEN
    Article testee = new Article(bot, title);

    // THEN
    assertEquals(title, testee.getTitle());
    assertEquals("test text", testee.getText());
  }

  @Test
  public void testInit_with_title_text() {
    // GIVEN
    WikiBot bot = mock(WikiBot.class);
    String title = "test title";
    String text = "test text";

    SimpleArticle mockSA = mock(SimpleArticle.class);
    when(mockSA.getText()).thenReturn(text);
    when(bot.readData(title)).thenReturn(mockSA);

    // WHEN
    Article testee = new Article(bot, text, title);

    // THEN
    assertEquals(title, testee.getTitle());
    assertEquals(text, testee.getText());
  }

  @Test
  public void testInit_with_simple() {
    // GIVEN
    WikiBot bot = mock(WikiBot.class);
    String title = "test title";
    String text = "test text";
    String revId = "revId";
    Date date = new Date(7);

    SimpleArticle mockSA = mock(SimpleArticle.class);
    when(mockSA.getText()).thenReturn(text);
    when(mockSA.getTitle()).thenReturn(title);
    when(mockSA.getEditTimestamp()).thenReturn(date);
    when(mockSA.getRevisionId()).thenReturn(revId);

    when(bot.readData(title)).thenReturn(mockSA);

    // WHEN
    Article testee = new Article(bot, mockSA);
    assertEquals(0, testee.reload);

    // THEN
    assertEquals(title, testee.getTitle());
    assertEquals(text, testee.getText());
    assertEquals(date, testee.getEditTimestamp());
    assertEquals(revId, testee.getRevisionId());
  }

  @Test
  public void testInit_with_simple_no_reload() {
    // GIVEN
    WikiBot bot = mock(WikiBot.class);
    String title = "test title";
    String text = "test text";
    String revId = "revId";
    Date date = new Date(7);

    SimpleArticle mockSA = mock(SimpleArticle.class);
    when(mockSA.getText()).thenReturn(text);
    when(mockSA.getTitle()).thenReturn(title);
    when(mockSA.getEditTimestamp()).thenReturn(date);
    when(mockSA.getRevisionId()).thenReturn(revId);

    // WHEN
    Article testee = Article.withoutReload(mockSA, bot);
    assertEquals(63, testee.reload);

    // THEN
    assertEquals(title, testee.getTitle());
    assertEquals(text, testee.getText());
    assertEquals(date, testee.getEditTimestamp());
    assertEquals(revId, testee.getRevisionId());
    Mockito.verifyNoMoreInteractions(bot);
  }
}
