/**
 * 
 */
package net.sourceforge.jwbf.mediawiki.live;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostDelete;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

/**
 * @author Thomas
 * 
 */
public class DeleteTest extends AbstractMediaWikiBotTest {
  private static final String DELETE_PREFIX = "Delete";
  private static final int COUNT = 1;

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      PostDelete.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  private void prepare(MediaWikiBot bot) {
    SimpleArticle a = new SimpleArticle();

    for (int i = 0; i < COUNT; i++) {
      a.setTitle(DELETE_PREFIX + i);
      a.setText(getRandom(23));
      bot.writeContent(a);
    }
  }

  private void delete(MediaWikiBot bot) throws ActionException,
      ProcessException {

    for (int i = 0; i < COUNT; i++) {
      bot.delete(DELETE_PREFIX + i);
    }
  }

  private void test(MediaWikiBot bot) throws ActionException, ProcessException {

    for (int i = 0; i < COUNT; i++) {
      ContentAccessable ca = bot.getArticle(DELETE_PREFIX + i);

      assertTrue("textlength of Delete " + i + " is greater then 0 ("
          + ca.getText().length() + ")", ca.getText().length() == 0);

    }
  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x15() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_15, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_15.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  /**
   * Test.
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void deleteWikiMW1x16() throws Exception {

    bot = BotFactory.getMediaWikiBot(Version.MW1_16, true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(),
        Version.MW1_16.equals(bot.getVersion()));

    prepare(bot);
    delete(bot);
    test(bot);
  }

  @Ignore
  @Test
  public void deleteAll() throws Exception {
    Version[] all = { Version.MW1_15, Version.MW1_16 };
    for (Version v : all) {
      MediaWikiBot bot = BotFactory.getMediaWikiBot(v, true);
      AllPageTitles aPages = new AllPageTitles(bot, MediaWiki.NS_MAIN);
      for (String string : aPages) {
        System.out.println(string);
      }
    }
  }
}
