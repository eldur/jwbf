package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;

import org.junit.Test;

public class BotFactoryTest {

  @Test
  public void testIoC() {
    BotFactory.getMediaWikiBot(Version.getLatest(), false);
  }
}
