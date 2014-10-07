package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryMembersSimpleTest {

  private CategoryMembers categoryMembers = mock(CategoryMembers.class);

  private MediaWikiBot bot = mock(MediaWikiBot.class);

  @Spy
  private CategoryMembersSimple testee = new CategoryMembersSimple(bot, categoryMembers);

  @Test
  public void testPrepareCollection() {
    // GIVE
    HttpAction expected = mock(HttpAction.class);
    when(categoryMembers.prepareNextRequest()).thenReturn(expected);

    // WHEN
    HttpAction result = testee.prepareNextRequest();

    // THEN
    assertEquals(expected, result);

  }

  @Test
  public void testParseArticleTitles() {
    // GIVE
    ImmutableList<String> mock = ImmutableList.of("a title");
    when(categoryMembers.parseArticles(Mockito.eq("valid"), Mockito.any(NonnullFunction.class))) //
        .thenReturn(mock);

    // WHEN
    ImmutableList<String> result = testee.parseElements("valid");

    // THEN
    GAssert.assertEquals(ImmutableList.of("a title"), result);

  }

  @Test
  public void testParseHasMore() {
    // GIVE
    when(categoryMembers.parseHasMore("valid")).thenReturn(Optional.of("a"));

    // WHEN
    Optional<String> result = testee.parseHasMore("valid");

    // THEN
    assertEquals(Optional.of("a"), result);

  }
}
