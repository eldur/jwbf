package net.sourceforge.jwbf.mediawiki.live.auto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.RandomPageTitle;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class RandomPageTitleTest extends ParamHelper {

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  public RandomPageTitleTest(Version v) {
    super(BotFactory.getIntegMediaWikiBot(v, true));
  }

  @Test
  public void test() {
    RandomPageTitle random = new RandomPageTitle(bot);
    String title = random.getTitle();
    assertNotNull(title);
    assertEquals(title, random.getTitle());
  }

}
