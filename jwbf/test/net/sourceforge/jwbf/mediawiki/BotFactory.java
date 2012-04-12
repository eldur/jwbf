package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BotFactory {

  public static MediaWikiBot getMediaWikiBot(final Version v,
      final boolean login) {

    Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(HttpBot.class).toInstance(new HttpBot(getWikiUrl(v)));
      }
    });
    MediaWikiBot bot = injector.getInstance(MediaWikiBot.class);

    if (login) {
      bot.login(getWikiUser(v), getWikiPass(v));
    }
    return bot;
  }

  public static String getWikiUser(Version v) {
    return LiveTestFather.getValue("wiki" + v.name() + "_user");
  }

  public static String getWikiPass(Version v) {
    return LiveTestFather.getValue("wiki" + v.name() + "_pass");
  }

  public static String getWikiUrl(Version v) {
    return LiveTestFather.getValue("wiki" + v.name() + "_url");
  }
}
