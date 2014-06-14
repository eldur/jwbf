package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValueOrSkip;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import net.sourceforge.jwbf.mediawiki.actions.util.ApiException;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Thomas Stock
 */
public class RevisionTest extends ParamHelper {

  @ClassRule
  public static VersionTestClassVerifier classVerifier =
      new VersionTestClassVerifier(GetRevision.class, PostModifyContent.class, GetApiToken.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public RevisionTest(Version v) {
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
    try {
      bot.getArticle(title, GetRevision.COMMENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with COMMENT receiving");
    }
    try {
      bot.getArticle(title, GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with CONTENT receiving");
    }
    try {
      bot.getArticle(title, GetRevision.FIRST | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with FIRST receiving");
    }
    try {
      bot.getArticle(title, GetRevision.IDS | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with IDS receiving");
    }
    try {
      bot.getArticle(title, GetRevision.LAST | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with LAST receiving");
    }
    try {
      bot.getArticle(title, GetRevision.TIMESTAMP | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with TIMESTAMP receiving");
    }
    try {
      bot.getArticle(title, GetRevision.USER | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with USER receiving");
    }

    try {
      bot.getArticle(title, GetRevision.FLAGS | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with FLAGS receiving");
    }

    // test with content length > 0
    ArticleMeta a = bot.getArticle(title);
    assertEquals(testText, a.getText());
    assertEquals(user, a.getEditor());
    assertTrue("should be greater then 0", a.getRevisionId().length() > 0);

    // test with content length <= 0
    testText = "";
    title = "767676885340589358058903589035";
    a = bot.getArticle(title);

    assertEquals(testText, a.getText());

  }

}
