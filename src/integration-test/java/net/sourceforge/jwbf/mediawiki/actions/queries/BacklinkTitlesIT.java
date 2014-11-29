package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BacklinkTitlesIT extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(BacklinkTitlesIT.class);

  private static final String BACKLINKS = "Backlinks";
  private static final int COUNT = 10;

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public BacklinkTitlesIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  protected final void doPreapare() {
    log.info("prepareing backlinks...");
    SimpleArticle a = new SimpleArticle();
    for (int i = 0; i <= COUNT; i++) {
      a.setTitle("Back" + i);
      if (i % 2 == 0) {
        a.setText("#redirect [[" + BACKLINKS + "]]");
      } else {
        a.setText("[[" + BACKLINKS + "]]");
      }
      bot.writeContent(a);
    }
    log.info("... done");
  }

  @Test
  public final void testAll() {

    ImmutableList<String> expected =
        ImmutableList.of("Back0", "Back1", "Back2", "Back3", "Back4", "Back5", "Back6", "Back7",
            "Back8", "Back9");
    doTest(expected, RedirectFilter.all);
  }

  @Test
  public final void testRedirects() {

    ImmutableList<String> expected =
        ImmutableList.of("Back0", "Back2", "Back4", "Back6", "Back8", "Back10", "Back12", "Back14",
            "Back16", "Back18");
    doTest(expected, RedirectFilter.redirects);
  }

  @Test
  public final void testNonRedirects() {

    ImmutableList<String> expected =
        ImmutableList.of("Back1", "Back3", "Back5", "Back7", "Back9", "Back11", "Back13", "Back15",
            "Back17", "Back19");
    doTest(expected, RedirectFilter.nonredirects);
  }

  private void doTest(ImmutableList<String> expected, RedirectFilter rf) {

    ImmutableList<Integer> namespaces = ImmutableList.of(MediaWiki.NS_MAIN, MediaWiki.NS_CATEGORY);
    BacklinkTitles gbt = new BacklinkTitles(bot, BACKLINKS, 5, rf, namespaces);

    List<String> vx = Lists.newArrayList();
    Iterator<String> is = gbt.iterator();
    boolean notEnougth = true;
    int i = 0;
    while (is.hasNext()) {
      is.next();
      i++;
      if (i > COUNT) {
        notEnougth = false;
        break;
      }
    }
    if (notEnougth) {
      log.warn(i + " backlinks are to less ( requred for test: " + COUNT + ")");
      doPreapare();
    }
    is = gbt.iterator();
    vx.add(is.next());
    vx.add(is.next());
    vx.add(is.next());
    is = gbt.iterator();
    i = 0;
    ImmutableList.Builder<String> actual = ImmutableList.builder();
    while (is.hasNext()) {
      String buff = is.next();
      vx.remove(buff);
      actual.add(buff);
      i++;
      if (i >= COUNT) {
        break;
      }
    }
    assertTrue("Iterator should contain: " + vx, vx.isEmpty());
    assertTrue("Fail: " + i + " < " + COUNT, i > COUNT - 1);
    GAssert.assertEquals(expected, actual.build());
  }

}
