package net.sourceforge.jwbf.mediawiki.actions.queries;

import com.google.common.primitives.Ints;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.AbstractMediaWikiBotTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Stock
 */
public class BacklinkTitlesVarLiveIntegTest extends AbstractMediaWikiBotTest {

  private static final Logger log = LoggerFactory.getLogger(BacklinkTitlesVarLiveIntegTest.class);

  @Test
  public final void backlinksWikipediaDe() {
    String url = getWikipediaDeUrl();
    bot = new MediaWikiBot(url);
    BacklinkTitles is = new BacklinkTitles(bot, LiveTestFather.getValueOrSkip("backlinks_article"));

    int i = 0;
    while (is.hasNext()) {
      is.next();
      i++;
      if (i > maxBacklinkArticleCount() + 1) {
        break;
      }
    }

    Assert.assertTrue("Fail: " + i + " <= " + maxBacklinkArticleCount(),
        i >= maxBacklinkArticleCount());
  }

  private int maxBacklinkArticleCount() {
    int intValue = getIntValue("backlinks_article_count");
    return intValue;
  }

  private static int getIntValue(final String key) {
    String value = LiveTestFather.getValueOrSkip(key);

    Integer intOrNull = Ints.tryParse(value);
    if (intOrNull == null) {
      return 0;
    } else {
      return intOrNull.intValue();
    }
  }
}
