package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.TestHelper.getRandom;

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;

/** @author Thomas Stock */
public class ImageUsageTitlesIT extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(ImageUsageTitlesIT.class);

  private static final int LIMIT = 4;
  public static final String IMAGE_NAME = "Image:Any.gif";

  @Parameterized.Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public ImageUsageTitlesIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  @Test
  public void test() {
    // GIVEN
    ImmutableList<String> expectedTitles = TestHelper.createNames("TitleWithImg", LIMIT);

    ImmutableList<String> initPageTitles =
        new ImageUsageTitles(bot(), 3, IMAGE_NAME, MediaWiki.NS_EVERY).getCopyOf(LIMIT);
    ImmutableList<String> pageTitles;
    if (initPageTitles.size() < LIMIT) {
      prepare(expectedTitles);
      pageTitles = new ImageUsageTitles(bot(), IMAGE_NAME).getCopyOf(LIMIT);
    } else {
      pageTitles = initPageTitles;
    }

    // THEN
    GAssert.assertEquals(expectedTitles, pageTitles);
  }

  private void prepare(ImmutableList<String> expectedTitles) {
    log.info("begin prepare");
    for (String name : expectedTitles) {
      Article a = new Article(bot, name);
      a.setText("Hello [[" + IMAGE_NAME + "]] - " + getRandom(10));
      a.save();
    }
  }
}
