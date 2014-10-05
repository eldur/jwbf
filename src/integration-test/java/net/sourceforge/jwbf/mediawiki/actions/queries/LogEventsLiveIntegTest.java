package net.sourceforge.jwbf.mediawiki.actions.queries;

import static net.sourceforge.jwbf.TestHelper.getRandom;
import static net.sourceforge.jwbf.TestHelper.getRandomAlph;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.contentRep.LogItem;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogEventsLiveIntegTest extends ParamHelper {

  private static final Logger log = LoggerFactory.getLogger(LogEventsLiveIntegTest.class);

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public LogEventsLiveIntegTest(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  private static final int LIMIT = 3;

  @Test
  public final void test() {
    // GIVEN
    ImmutableList<String> deletedTitles = doPrepare(bot);

    // WHEN
    ImmutableList<LogItem> items = new LogEvents(bot, 1, LogEvents.DELETE).getCopyOf(LIMIT);

    // THEN
    ImmutableList<String> expected = ImmutableList.copyOf(Lists.reverse(deletedTitles));
    if (bot.getVersion().greaterEqThen(Version.MW1_23)) {
      GAssert.assertEquals(expected, FluentIterable.from(items) //
          .transform(LogEvents.toTitles()) //
          .toList());
    } else {
      GAssert.assertEquals(expected.subList(0, 1),
          FluentIterable.from(items).transform(LogEvents.toTitles()).toList());

    }
  }

  private static ImmutableList<String> doPrepare(MediaWikiBot bot) {
    log.warn("prepare");
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    for (int i = 0; i < LIMIT; i++) {
      String title = getRandomAlph(6);
      builder.add(title);
      Article a = new Article(bot, title);
      a.setText(getRandom(5));
      a.save();
      assertTrue("content should be", a.getText().length() > 0);
      a.delete();
    }
    return builder.build();
  }

}
