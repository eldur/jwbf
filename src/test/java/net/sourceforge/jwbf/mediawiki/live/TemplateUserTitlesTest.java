package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Vector;

import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.TemplateUserTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class TemplateUserTitlesTest extends AbstractMediaWikiBotTest {

  private static final String TESTPATTERNNAME = "Template:ATesT";

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      TemplateUserTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Test
  public final void templateUserWikiMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doRegularTest();

    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_15.equals(bot.getVersion()));
  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void templateUserWikiMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doRegularTest();

    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_16.equals(bot.getVersion()));
  }

  private void doRegularTest() throws JwbfException {
    TemplateUserTitles a = new TemplateUserTitles(bot, TESTPATTERNNAME,
        MediaWiki.NS_ALL);

    int i = 0;
    Collection<String> titles = new Vector<String>();

    for (int j = 0; j < 55; j++) {
      titles.add("Patx" + j);
    }

    for (@SuppressWarnings("unused")
    String pageTitle : a) {
      pageTitle += "";
      i++;
    }
    if (i < 50) {
      prepare(bot, titles);
    }

    for (String pageTitle : a) {
      titles.remove(pageTitle);
      System.out.println(titles);
      i++;
    }
    if (i < 50) {
      fail("to less " + i);
    }
    assertTrue("title collection should be empty", titles.isEmpty());

    Article template = new Article(bot, TESTPATTERNNAME);
    assertEquals(TESTPATTERNNAME + " content ", "a test", template.getText());
  }

  private void prepare(MediaWikiBot bot, Collection<String> titles)
      throws JwbfException {
    Article template = new Article(bot, TESTPATTERNNAME);
    template.setText("a test");
    template.save();

    for (String title : titles) {
      Article a = new Article(bot, title);
      a.setText(getRandom(1) + " {{" + TESTPATTERNNAME + "}}");
      a.save();
    }

  }
}
