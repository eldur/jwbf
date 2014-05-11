package net.sourceforge.jwbf.mediawiki.live.auto;

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

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

// TODO move to integ tests
public class AllPagesExpTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      AllPageTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public AllPagesExpTest(Version v) {
    super(v, classVerifier);
  }

  @Test
  public void doTest() {
    AllPageTitles allPages = new AllPageTitles(bot, null, null, RedirectFilter.all,
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

    Iterator<String> allPagesIterator = allPages.iterator();
    int i = 0;
    while (allPagesIterator.hasNext()) {
      String title = allPagesIterator.next();
      specialChars.remove(title);
      i++;
      if (i > 55) {
        break;
      }
    }

    for (char c : MediaWikiBot.INVALID_LABEL_CHARS) {
      specialChars.remove(c + "");
    }
    if (!specialChars.isEmpty()) {
      while (allPagesIterator.hasNext() || !specialChars.isEmpty()) {
        specialChars.remove(allPagesIterator.next());
      }
    }

    assertTrue("tc sould be empty but is: " + specialChars, specialChars.isEmpty());
    assertTrue("i is: " + i + " but should be greater than", i > 50);

  }

}
