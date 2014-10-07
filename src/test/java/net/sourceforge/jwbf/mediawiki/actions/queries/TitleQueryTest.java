package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.mockito.Mockito.mock;

import java.util.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Test;

public class TitleQueryTest {

  @Test
  public void testInit() {
    MediaWikiBot bot = mock(MediaWikiBot.class);
    new TitleQuery<String>(bot) {

      @Override
      protected Iterator<String> copy() {
        return ImmutableList.<String>of().iterator();
      }

      @Override
      protected HttpAction prepareNextRequest() {
        return null;
      }

      @Override
      protected ImmutableList<String> parseElements(String s) {
        return ImmutableList.of();
      }

      @Override
      protected Optional<String> parseHasMore(String s) {
        return Optional.absent();
      }
    };
  }

}
