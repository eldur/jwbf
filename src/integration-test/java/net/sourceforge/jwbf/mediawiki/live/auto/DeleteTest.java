package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostDelete;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTest extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(DeleteTest.class);
  private static final String DELETE_PREFIX = "Delete";
  private static final int COUNT = 1;

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      PostDelete.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public DeleteTest(Version v) {
    super(v, classVerifier);
  }

  private void prepare(MediaWikiBot bot) {
    SimpleArticle a = new SimpleArticle();

    for (int i = 0; i < COUNT; i++) {
      a.setTitle(DELETE_PREFIX + i);
      a.setText(getRandom(23));
      bot.writeContent(a);
    }
  }

  private void delete(MediaWikiBot bot) {

    for (int i = 0; i < COUNT; i++) {
      bot.delete(DELETE_PREFIX + i);
    }
  }

  private void test(MediaWikiBot bot) {

    for (int i = 0; i < COUNT; i++) {
      ContentAccessable ca = bot.getArticle(DELETE_PREFIX + i);

      assertTrue(
          "textlength of Delete " + i + " is greater then 0 (" + ca.getText().length() + ")", ca
          .getText().length() == 0);

    }
  }

  @Test
  public final void delete() {

    prepare(bot);
    delete(bot);
    test(bot);
  }

  @Ignore
  @Test
  public void deleteAll() {
    Version[] all = { Version.MW1_15, Version.MW1_16 };
    for (Version v : all) {
      MediaWikiBot bot = BotFactory.getMediaWikiBot(v, true);
      AllPageTitles aPages = new AllPageTitles(bot, MediaWiki.NS_MAIN);
      for (String string : aPages) {
        log.debug(string);
      }
    }
  }
}
