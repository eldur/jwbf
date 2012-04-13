package net.sourceforge.jwbf.mediawiki.live;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.queries.BacklinkTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Thomas Stock
 * 
 */
@Slf4j
public class BacklinkTest extends AbstractMediaWikiBotTest {

  /**
   * Test backlinks.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void backlinksWikipediaDe() throws Exception {
    String url = "http://de.wikipedia.org/w/index.php";
    LiveTestFather.assumeReachable(url);
    bot = new MediaWikiBot(url);
    BacklinkTitles is = new BacklinkTitles(bot,
        LiveTestFather.getValue("backlinks_article"));

    int i = 0;
    while (is.hasNext()) {
      is.next();
      i++;
      if (i > getIntValue("backlinks_article_count") + 1) {
        break;
      }
    }

    Assert.assertTrue("Fail: " + i + " < "
        + getIntValue("backlinks_article_count"),
        i > getIntValue("backlinks_article_count"));
  }

  private static int getIntValue(final String key) throws Exception {
    return Integer.parseInt(LiveTestFather.getValue(key));
  }
}
