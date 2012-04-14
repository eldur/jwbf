package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import net.sourceforge.jwbf.mediawiki.live.AbstractMediaWikiBotTest;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;

public class EditTest extends AbstractMediaWikiBotTest {

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      PostModifyContent.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Test
  public final void categoryWikiMWLast() {

    bot = getMediaWikiBot(Version.getLatest(), true);
    assertTrue("Wrong Wiki Version " + bot.getVersion(), Version.getLatest()
        .equals(bot.getVersion()));
    doTest();

  }

  
  // TODO change to an autotest
  private void doTest() throws ActionException, ProcessException {
    SimpleArticle sa = new SimpleArticle("Test");
    sa.setText(getRandom(5));
    PostModifyContent pmc = new PostModifyContent(bot, sa);

    bot.performAction(pmc);
    assertTrue("i is: ", 50 > 3);

  }

}
