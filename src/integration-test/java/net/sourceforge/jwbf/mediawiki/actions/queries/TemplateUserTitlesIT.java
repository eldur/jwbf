package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.TestHelper.getRandom;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Stock
 */
public class TemplateUserTitlesIT extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(TemplateUserTitlesIT.class);

  private static final String ARTICLE_NAME_PREFIX = "TestTemplate";
  private static final String TESTPATTERNNAME = "Template:" + ARTICLE_NAME_PREFIX;

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public TemplateUserTitlesIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  @Test
  public void doRegularTest() {
    TemplateUserTitles testee = //
        new TemplateUserTitles(bot, 3, TESTPATTERNNAME, MWAction.nullSafeCopyOf(MediaWiki.NS_ALL));

    ImmutableList<String> titles = testTitles(4);
    ImmutableList<String> backlinkTitles = testee.getCopyOf(4);

    if (!backlinkTitles.equals(titles)) {
      prepare(bot, titles);
      GAssert.assertEquals(titles, testee.getCopyOf(4));
    } else {
      GAssert.assertEquals(titles, backlinkTitles);
    }
  }

  private ImmutableList<String> testTitles(int limit) {
    List<String> temp = Lists.newArrayList();

    for (int j = 0; j < limit; j++) {
      temp.add(ARTICLE_NAME_PREFIX + j);
    }
    return ImmutableList.copyOf(temp);
  }

  private void prepare(MediaWikiBot bot, ImmutableList<String> titles) {
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
