package net.sourceforge.jwbf.mediawiki.actions.editing;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;

public class PostDeleteIT extends ParamHelper {

  private static final String DELETE_PREFIX = "Delete";
  private static final int LIMIT = 1;

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public PostDeleteIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  private void createArticles(ImmutableList<String> titles) {
    for (String title : titles) {
      SimpleArticle a = new SimpleArticle();
      a.setTitle(title);
      a.setText(getRandom(23));
      bot().writeContent(a);
    }
  }

  private void delete(ImmutableList<String> titles) {
    for (String title : titles) {
      bot().delete(title);
    }
  }

  private void checkAbsent(ImmutableList<String> titles) {
    for (String title : titles) {
      ContentAccessable ca = bot.getArticle(title);
      assertTrue(
          "textlength of " + title + " is greater then 0 (" + ca.getText().length() + ")",
          ca.getText().length() == 0);
    }
  }

  @Test
  public final void delete() {
    // GIVEN
    ImmutableList<String> titles = TestHelper.createNames(DELETE_PREFIX, LIMIT);
    createArticles(titles);
    // WHEN
    delete(titles);
    // THEN
    checkAbsent(titles);
  }
}
