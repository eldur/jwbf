package net.sourceforge.jwbf.mediawiki.live;

import javax.inject.Provider;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public abstract class AbstractMediaWikiBotTest implements
    Provider<MediaWikiBot> {
  protected MediaWikiBot bot = null;

  public MediaWikiBot get() {
    return bot;
  }

}
