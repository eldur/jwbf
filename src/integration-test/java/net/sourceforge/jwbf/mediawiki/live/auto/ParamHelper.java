package net.sourceforge.jwbf.mediawiki.live.auto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.VersionTestClassVerifier;
import net.sourceforge.jwbf.mediawiki.live.AbstractMediaWikiBotTest;
import org.junit.Assert;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class ParamHelper extends AbstractMediaWikiBotTest {

  public ParamHelper(Version v, VersionTestClassVerifier verifier) {
    try {
      version(v);
      bot(BotFactory.getMediaWikiBot(version(), true));
    } catch (AssumptionViolatedException assumptionFailed) {
      verifier.addIgnoredVersion(version());
      throw assumptionFailed;
    }
    Assert.assertEquals(v, bot().getVersion());
  }

  public static Collection<?> prepare(List<Version> versions) {

    Object[][] objects = new Object[versions.size()][1];
    int i = 0;
    for (Version version : versions) {
      objects[i++][0] = version;
    }
    return Arrays.asList(objects);
  }
}
