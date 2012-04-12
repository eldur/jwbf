package net.sourceforge.jwbf.mediawiki;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BotFactory {

  public static MediaWikiBot getMediaWikiBot(final Version v,
      final boolean login) {

    final String wikiUrl = getWikiUrl(v);
    TestHelper.assumeReachable(wikiUrl);
    Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(HttpBot.class).toInstance(
            new RecordingHttpBot(new HttpBot(wikiUrl)));
      }
    });
    MediaWikiBot bot = injector.getInstance(MediaWikiBot.class);

    if (login) {
      bot.login(getWikiUser(v), getWikiPass(v));
    }
    return bot;
  }

  @Slf4j
  private static class RecordingHttpBot extends HttpBot {

    public RecordingHttpBot(HttpBot bot) {
      super(bot.getUrl());
    }

    @Override
    public synchronized String performAction(ContentProcessable a) {
      log.debug("{}", a);
      return super.performAction(a);
    }
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
