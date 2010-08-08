package net.sourceforge.jwbf.mediawiki.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.core.contentRep.ArticleMeta;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.LiveTestFather;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetRevision;
import net.sourceforge.jwbf.mediawiki.actions.editing.PostModifyContent;
import net.sourceforge.jwbf.mediawiki.actions.util.ApiException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class RevisionTest extends LiveTestFather {

  private MediaWikiBot bot;

  /**
   * Setup log4j.
   * @throws Exception a
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    TestHelper.prepareLogging();
    addInitSupporterVersions(GetRevision.class);
    addInitSupporterVersions(PostModifyContent.class);
    addInitSupporterVersions(GetApiToken.class);

  }




  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x09() throws Exception {
    bot = getMediaWikiBot(Version.MW1_09, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x10() throws Exception {
    bot = getMediaWikiBot(Version.MW1_10, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x11() throws Exception {
    bot = getMediaWikiBot(Version.MW1_11, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x12() throws Exception {
    bot = getMediaWikiBot(Version.MW1_12, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x13() throws Exception {
    bot = getMediaWikiBot(Version.MW1_13, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x14() throws Exception {
    bot = getMediaWikiBot(Version.MW1_14, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x15() throws Exception {
    bot = getMediaWikiBot(Version.MW1_15, true);
    doTest(bot);
  }

  /**
   * Test write and read.
   * @throws Exception a
   */
  @Test
  public final void getRevisionMW1x16() throws Exception {
    bot = getMediaWikiBot(Version.MW1_16, true);
    doTest(bot);
  }

  private void doTest(MediaWikiBot bot) throws Exception {

    String title = getValue("wikiMW1_12_user");
    String user = bot.getUserinfo().getUsername();
    SimpleArticle sa;
    // write init content
    String testText = getRandom(255);
    sa = new SimpleArticle(title);
    sa.setText(testText);
    bot.writeContent(sa);
    // Test parameters
    try {
      bot.readContent(title, GetRevision.COMMENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with COMMENT receiving");
    }
    try {
      bot.readContent(title, GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with CONTENT receiving");
    }
    try {
      bot.readContent(title, GetRevision.FIRST | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with FIRST receiving");
    }
    try {
      bot.readContent(title, GetRevision.IDS | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with IDS receiving");
    }
    try {
      bot.readContent(title, GetRevision.LAST | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with LAST receiving");
    }
    try {
      bot.readContent(title, GetRevision.TIMESTAMP | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with TIMESTAMP receiving");
    }
    try {
      bot.readContent(title, GetRevision.USER | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with USER receiving");
    }

    try {
      bot.readContent(title, GetRevision.FLAGS | GetRevision.CONTENT);
    } catch (ApiException e) {
      throw new JwbfException("Problems with FLAGS receiving");
    }





    // test with content length > 0
    ArticleMeta a = bot.readContent(title);
    assertEquals(testText, a.getText());
    assertEquals(user, a.getEditor());
    assertTrue("should be greater then 0", a.getRevisionId().length() > 0);

    // test with content length <= 0
    testText = "";
    title = "767676885340589358058903589035";
    a = bot.readContent(title);

    assertEquals(testText, a.getText());
    registerTestedVersion(GetRevision.class, bot.getVersion());
    registerTestedVersion(PostModifyContent.class, bot.getVersion());
    if (bot.getVersion().greaterEqThen(Version.MW1_12)) {
      registerTestedVersion(GetApiToken.class, bot.getVersion());
    }

  }

}
