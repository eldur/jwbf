package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.CategoryItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryMembersTest {

  private ImmutableList<Integer> ns = ImmutableList.of(MediaWiki.NS_MAIN);
  @Spy
  private CategoryMembers testee = new CategoryMembers(Mockito.mock(MediaWikiBot.class), "A", ns) {
    @Override
    protected Iterator<CategoryItem> copy() {
      return null;
    }

    @Override
    protected HttpAction prepareNextRequest() {
      return null;
    }
  };

  @Test
  public void testParseArticleTitles() {
    // GIVEN / WHEN
    ImmutableList<CategoryItem> result = testee.parseElements(BaseQueryTest.emptyXml());

    // THEN
    assertTrue(result.isEmpty());
  }
}
