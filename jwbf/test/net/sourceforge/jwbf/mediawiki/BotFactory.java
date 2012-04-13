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

    Injector injector = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(HttpBot.class).toInstance(
            new CacheHttpBot(new HttpBot(wikiUrl), new WireRegister()));
      }
    });
    MediaWikiBot bot = injector.getInstance(MediaWikiBot.class);

    if (login) {
      bot.login(getWikiUser(v), getWikiPass(v));
    }
    return bot;
  }

  @Slf4j
  private static class CacheHttpBot extends HttpBot {

    private final WireRegister wireRegister;

    public CacheHttpBot(HttpBot bot, WireRegister wireRegister) {
      super(bot.getUrl());
      this.wireRegister = wireRegister;
      if (wireRegister.hasContentFor(bot.getUrl())) {
        TestHelper.assumeReachable(bot.getUrl()); // TODO this is an error
      }
    }

    @Override
    public synchronized String performAction(ContentProcessable a) {
      log.debug("{}", a);
      String response = wireRegister.getResponse(a);
      if (response != null) {
        return response;
      } else {
        return wireRegister.putResponse(super.performAction(a));
      }
    }
  }

  public static String getWikiUser(Version v) {
    return "Admin";
  }

  public static String getWikiPass(Version v) {
    return "nimdA";
  }

  public static String getWikiUrl(Version v) {
    String local = LiveTestFather.getValue("localwikihost");
    String version = "mw-" + v.getNumber().replace(".", "-");
    return "http://" + local + "/" + version + "/index.php";
  }
}
