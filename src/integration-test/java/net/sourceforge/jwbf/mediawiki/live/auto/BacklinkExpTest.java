package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.queries.BacklinkTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BacklinkExpTest extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(BacklinkExpTest.class);

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      BacklinkTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  private static final String BACKLINKS = "Backlinks";
  private static final int COUNT = 60;

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public BacklinkExpTest(Version v) {
    super(v, classVerifier);
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

  /**
   * Test backlinks.
   */
  @Test
  public final void test() {
    doTest(RedirectFilter.all);
  }

  private void doTest(RedirectFilter rf) {

    BacklinkTitles gbt = new BacklinkTitles(bot, BACKLINKS, rf, MediaWiki.NS_MAIN,
        MediaWiki.NS_CATEGORY);

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
    while (is.hasNext()) {
      String buff = is.next();
      vx.remove(buff);
      i++;
      if (i > COUNT) {
        break;
      }
    }
    assertTrue("Iterator should contain: " + vx, vx.isEmpty());
    assertTrue("Fail: " + i + " < " + COUNT, i > COUNT - 1);

  }

}
