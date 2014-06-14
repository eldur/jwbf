package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValueOrSkip;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.queries.ImageUsageTitles;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Stock
 */
@Ignore("depends on upload")
public class ImageUsageTitlesTest extends AbstractMediaWikiBotTest {

  private static final Logger log = LoggerFactory.getLogger(ImageUsageTitlesTest.class);

  @ClassRule
  public static VersionTestClassVerifier classVerifier =
      new VersionTestClassVerifier(ImageUsageTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  private static final int limit = 55;

  @Test
  public final void imageUsageMW1x15() throws Exception {

    bot = getMediaWikiBot(Version.MW1_15, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_15.equals(bot.getVersion()));
    test();

  }

  @Test
  public final void imageUsageMW1x16() throws Exception {

    bot = getMediaWikiBot(Version.MW1_16, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.MW1_16.equals(bot.getVersion()));
    test();

  }

  private void test() {
    ImageUsageTitles il =
        new ImageUsageTitles(bot, "Image:" + getValueOrSkip("filename"), MediaWiki.NS_ALL);

    boolean notFound = true;
    int x = 0;
    for (String string : il) {
      log.debug(string);
      x++;
      if (x >= limit) {
        notFound = false;
        break;
      }
    }
    if (notFound) {
      prepare();
    }
    x = 0;
    for (String string : il) {
      log.debug(string);
      x++;
      if (x >= limit) {
        break;
      }
    }

    if (x < limit) {
      fail("limit" + x);
    }

  }

  private void prepare() {

    String name = "";
    for (int i = 0; i < limit; i++) {
      name = "TitleWithImg" + i;
      Article a = new Article(bot, name);
      a.setText("Hello [[Image:" + getValueOrSkip("filename") + "]] a image " + getRandom(10));
      a.save();
    }

  }
}
