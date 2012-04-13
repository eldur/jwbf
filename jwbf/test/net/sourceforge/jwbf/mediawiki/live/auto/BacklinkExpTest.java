package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.BacklinkTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.test.SimpleNameFinder;
import net.sourceforge.jwbf.test.TestNamer;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

@Slf4j
@TestNamer(SimpleNameFinder.class)
public class BacklinkExpTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      BacklinkTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  private static final String BACKLINKS = "Backlinks";
  private static final int COUNT = 60;

  @Parameters
  public static Collection<?> regExValues() {
    return ParamHelper.prepare(MW1_15, MW1_16, MW1_17, MW1_18);
  }

  public BacklinkExpTest(Version v) {
    super(v);
  }

  protected final void doPreapare() throws ActionException, ProcessException {
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
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void test() throws Exception {
    doTest(RedirectFilter.all);
  }

  private void doTest(RedirectFilter rf) {

    BacklinkTitles gbt = new BacklinkTitles(bot, BACKLINKS, rf,
        MediaWiki.NS_MAIN, MediaWiki.NS_CATEGORY);

    Vector<String> vx = new Vector<String>();
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
