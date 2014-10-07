package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BacklinkTitlesTest {

  @Spy
  private BacklinkTitles testee = new BacklinkTitles(mock(MediaWikiBot.class), "");

  @Test
  public void testParseArticleTitles() {
    // GIVEN / WHEN
    ImmutableList<String> result = testee.parseElements(BaseQueryTest.emptyXml());

    // THEN
    assertTrue(result.isEmpty());
  }

}
