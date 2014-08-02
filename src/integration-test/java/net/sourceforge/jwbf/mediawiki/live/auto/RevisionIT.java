package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValueOrSkip;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Thomas Stock
 */
public class RevisionIT extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier =
      new VersionTestClassVerifier(GetRevision.class, PostModifyContent.class, GetApiToken.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public RevisionIT(Version v) {
    super(v, classVerifier);
  }

  /**
   * Test write and read.
   */
  @Test
  public void doTest() throws Exception {

    String title = getValueOrSkip("wikiMW1_12_user");
    String user = bot.getUserinfo().getUsername();
    SimpleArticle sa;
    // write init content
    String testText = getRandom(255);
    sa = new SimpleArticle(title);
    sa.setText(testText);
    bot.writeContent(sa);
    // Test parameters
    bot.getArticle(title, GetRevision.COMMENT); // XXX delete this line

    // test with content length > 0
    ArticleMeta a = bot.getArticle(title);
    assertEquals(testText, a.getText());
    assertEquals(user, a.getEditor());
    assertTrue("should be greater then 0", a.getRevisionId().length() > 0);

    // test with content length <= 0
    String newTitle = "767676885340589358058903589035";
    a = bot.getArticle(newTitle);

    assertEquals("", a.getText());

  }

}
