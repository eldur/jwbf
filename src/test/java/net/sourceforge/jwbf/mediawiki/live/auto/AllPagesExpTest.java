package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.RedirectFilter;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.test.SimpleNameFinder;
import net.sourceforge.jwbf.test.TestNamer;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

@TestNamer(SimpleNameFinder.class)
public class AllPagesExpTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      AllPageTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters
  public static Collection<?> regExValues() {
    return ParamHelper.prepare(MW1_15, MW1_16, MW1_17, MW1_18);
  }

  public AllPagesExpTest(Version v) {
    super(v);
  }

  @Test
  public void doTest() {
    AllPageTitles gat = new AllPageTitles(bot, null, null, RedirectFilter.all,
        MediaWiki.NS_MAIN);

    SimpleArticle sa;
    String testText = TestHelper.getRandom(255);

    Collection<String> specialChars = LiveTestFather.getSpecialChars();
    try {
      for (String title : specialChars) {
        sa = new SimpleArticle(title);
        sa.setText(testText);
        bot.writeContent(sa);
      }
    } catch (ActionException e) {
      boolean found = false;
      for (char ch : MediaWikiBot.INVALID_LABEL_CHARS) {
        if (e.getMessage().contains(ch + "")) {
          found = true;
          break;
        }
      }
      assertTrue("should be a know invalid char", found);
    }

    Iterator<String> is = gat.iterator();
    int i = 0;
    while (is.hasNext()) {
      String nx = is.next();
      specialChars.remove(nx);
      i++;
      if (i > 55) {
        break;
      }
    }

    for (char c : MediaWikiBot.INVALID_LABEL_CHARS) {
      specialChars.remove(c + "");
    }
    if (!specialChars.isEmpty()) {
      while (is.hasNext() || !specialChars.isEmpty()) {
        specialChars.remove(is.next());
      }
    }

    assertTrue("tc sould be empty but is: " + specialChars,
        specialChars.isEmpty());
    assertTrue("i is: " + i, i > 50);

  }

}
