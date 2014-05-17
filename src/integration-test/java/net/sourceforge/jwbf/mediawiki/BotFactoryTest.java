package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import org.junit.Test;

public class BotFactoryTest {

  @Test
  public void testIoC() {
    BotFactory.getMediaWikiBot(Version.getLatest(), false);
  }
}
