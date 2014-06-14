package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CategoryMembersFullTest {

  @Test
  public void test_init() {
    // GIVEN
    MediaWikiBot bot = Mockito.mock(MediaWikiBot.class);
    String categoryName = "Test It";

    // WHEN
    CategoryMembersFull testee = new CategoryMembersFull(bot, categoryName);

    // THEN
    assertEquals(bot, testee.bot);
    assertEquals("Test_It", testee.categoryName);
    GAssert.assertEquals(ImmutableList.<Integer>of(), testee.namespace);
    assertFalse(testee.iterator().hasNext());

  }

  @Test
  public void test_innerFail() {
    // GIVEN
    MediaWikiBot bot = Mockito.mock(MediaWikiBot.class);
    IllegalStateException exception = new IllegalStateException();
    Mockito.when(bot.getPerformedAction(Mockito.any(CategoryMembersFull.class)))
        .thenThrow(exception);
    String categoryName = "Test It";

    // WHEN
    CategoryMembersFull testee = new CategoryMembersFull(bot, categoryName, 1);

    // THEN
    GAssert.assertEquals(ImmutableList.of(1), testee.namespace);
    try {
      testee.iterator().hasNext();
      fail();
    } catch (IllegalStateException e) {
      assertEquals(exception, e);
    }
  }

  @Test
  public void test() {
    // GIVEN
    String categoryName = "Test It";
    MediaWikiBot bot = mock(MediaWikiBot.class);
    final CategoryMembersFull testee = new CategoryMembersFull(bot, categoryName);
    final CategoryMembersFull value = mock(CategoryMembersFull.class);
    // XXX strage testdata
    String category = "<cm pageid=\"2\" ns=\"1\" title=\"a\" />";
    String withMore =
        "<query-continue><categorymembers cmcontinue=\"5\" /></query-continue>" + category;
    Mockito.when(bot.getPerformedAction(Mockito.any(CategoryMembersFull.class)))
        .thenAnswer(categoryWith(testee, value, withMore))
        .thenAnswer(categoryWith(testee, value, category));

    // WHEN/THEN
    Iterator<CategoryItem> iterator = testee.iterator();
    assertTrue(iterator.hasNext());
    CategoryItem next = iterator.next();
    assertEquals("a", next.getTitle());
    assertEquals(1, next.getNamespace());
    assertEquals(2, next.getPageid());
    assertTrue(iterator.hasNext());
    iterator.next();
    assertFalse(iterator.hasNext());

  }

  private Answer<CategoryMembersFull> categoryWith(final CategoryMembersFull testee,
      final CategoryMembersFull value, final String text) {
    return new Answer<CategoryMembersFull>() {

      @Override
      public CategoryMembersFull answer(InvocationOnMock invocation) throws Throwable {

        testee.processAllReturningText(text);
        return value;
      }
    };
  }

}
