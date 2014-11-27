package net.sourceforge.jwbf.mediawiki.actions.meta;

import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.Set;

import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;
import org.junit.Test;
import org.junit.runners.Parameterized;

public class GetUserinfoIT extends ParamHelper {

  @Parameterized.Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(MediaWiki.Version.valuesStable());
  }

  public GetUserinfoIT(MediaWiki.Version v) {
    super(BotFactory.getMediaWikiBot(v, true));
  }

  @Test
  public void testGet() {
    Userinfo userinfo = bot().getUserinfo();

    Set<String> rights = userinfo.getRights();

    assertFalse(rights.isEmpty());
  }

}
