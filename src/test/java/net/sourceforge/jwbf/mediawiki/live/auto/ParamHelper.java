package net.sourceforge.jwbf.mediawiki.live.auto;

import static net.sourceforge.jwbf.mediawiki.BotFactory.getMediaWikiBot;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.queries.AllPageTitles;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.live.AbstractMediaWikiBotTest;

import org.junit.Assert;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
class ParamHelper extends AbstractMediaWikiBotTest {

  public ParamHelper(Version v, VersionTestClassVerifier verifier) {
    try {
      bot = getMediaWikiBot(v, true);
    } catch (AssumptionViolatedException re) {
      verifier.addIgnoredVersion(v);
      throw re;
    }
    Assert.assertEquals(v, bot.getVersion());
  }

  static Collection<?> prepare(Version... versions) {

    Version[] documentedVersions = MWAction.findSupportedVersions(AllPageTitles.class); // TODO
                                                                                        // maybe

    Object[][] objects = new Object[versions.length][1];
    for (int i = 0; i < versions.length; i++) {
      objects[i][0] = versions[i];

    }

    return Arrays.asList(objects);
  }
}
