package net.sourceforge.jwbf.mediawiki.actions.queries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;

public class RandomPageTitlesIT extends ParamHelper {

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public RandomPageTitlesIT(Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  @Test
  public void test() {
    RandomPageTitle random = new RandomPageTitle(bot);
    String title = random.getTitle();
    assertNotNull(title);
    assertEquals(title, random.getTitle());
  }
}
