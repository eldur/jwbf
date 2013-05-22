package net.sourceforge.jwbf.mediawiki.live;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.queries.BacklinkTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.primitives.Ints;

/**
 * 
 * @author Thomas Stock
 * 
 */
@Slf4j
public class BacklinkTest extends AbstractMediaWikiBotTest {

  @Test
  public final void backlinksWikipediaDe() throws Exception {
    String url = getWikipediaDeUrl();
    bot = new MediaWikiBot(url);
    BacklinkTitles is = new BacklinkTitles(bot, LiveTestFather.getValue("backlinks_article"));

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

  private int maxBacklinkArticleCount() throws Exception {
    int intValue = getIntValue("backlinks_article_count");
    return intValue;
  }

  private static int getIntValue(final String key) {
    String value = LiveTestFather.getValue(key);

    Integer intOrNull = Ints.tryParse(value);
    if (intOrNull == null) {
      return 0;
    } else {
      return intOrNull.intValue();
    }
  }
}
