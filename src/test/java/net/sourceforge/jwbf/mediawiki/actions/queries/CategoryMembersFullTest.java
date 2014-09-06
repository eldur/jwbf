package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.mockito.Mockito;

public class CategoryMembersFullTest {

  @Test
  public void test_init() {
    // GIVEN
    MediaWikiBot bot = Mockito.mock(MediaWikiBot.class);
    String categoryName = "Test It";

    // WHEN
    CategoryMembersFull testee = new CategoryMembersFull(bot, categoryName);

    // THEN
    assertEquals(bot, testee.bot());
    assertEquals("Test_It", testee.categoryName);
    GAssert.assertEquals(ImmutableList.<Integer>of(), testee.namespace);
    assertFalse(testee.iterator().hasNext());

  }

  @Test
  public void test_innerFail() {
    // GIVEN
    MediaWikiBot bot = Mockito.mock(MediaWikiBot.class);
    IllegalStateException exception = new IllegalStateException();
    Mockito.when(bot.getPerformedAction(Mockito.any(BaseQuery.TitleQueryAction.class))) //
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

}
