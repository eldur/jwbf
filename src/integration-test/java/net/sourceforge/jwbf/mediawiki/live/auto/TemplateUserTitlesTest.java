package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import com.google.common.collect.Lists;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.TemplateUserTitles;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Stock
 */
public class TemplateUserTitlesTest extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitlesTest.class);

  private static final String TESTPATTERNNAME = "Template:ATesT";

  @ClassRule
  public static VersionTestClassVerifier classVerifier = new VersionTestClassVerifier(
      TemplateUserTitles.class);

  @Rule
  public Verifier successRegister = classVerifier.getSuccessRegister(this);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public TemplateUserTitlesTest(Version v) {
    super(v, classVerifier);
  }

  @Test
  public void doRegularTest() {
    TemplateUserTitles a = new TemplateUserTitles(bot, TESTPATTERNNAME, MediaWiki.NS_ALL);

    int i = 0;
    Collection<String> titles = Lists.newArrayList();

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
      log.debug(titles.toString());
      i++;
    }
    if (i < 50) {
      fail("to less " + i);
    }
    assertTrue("title collection should be empty", titles.isEmpty());

    Article template = new Article(bot, TESTPATTERNNAME);
    assertEquals(TESTPATTERNNAME + " content ", "a test", template.getText());
  }

  private void prepare(MediaWikiBot bot, Collection<String> titles) {
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
