package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

public class BotFactory {

  public static MediaWikiBot getMediaWikiBot(Version v, final boolean login) throws Exception {
    MediaWikiBot bot = new MediaWikiBot(getWikiUrl(v));
    if (login) {
      bot.login(getWikiUser(v), getWikiPass(v));
    }
    return bot;
  }
  public static String getWikiUser(Version v) throws Exception {
    return LiveTestFather.getValue("wiki" + v.name() + "_user");
  }
  public static String getWikiPass(Version v) throws Exception {
    return LiveTestFather.getValue("wiki" + v.name() + "_pass");
  }
  public static String getWikiUrl(Version v) throws Exception {
    return LiveTestFather.getValue("wiki" + v.name() + "_url");
  }
}
