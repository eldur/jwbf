package net.sourceforge.jwbf.mediawiki;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class BotFactory {

  private static Injector masterInjector = Guice.createInjector(new AbstractModule() {

    @Override
    protected void configure() {
      bind(WireRegister.class);

    }
  });

  public static MediaWikiBot getMediaWikiBot(final Version v, final boolean login) {

    final String wikiUrl = getWikiUrl(v);

    Injector injector = masterInjector.createChildInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(String.class).annotatedWith(Names.named("httpUrl")).toInstance(wikiUrl);
        bind(HttpBot.class).to(CacheHttpBot.class);

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
    private final String url;

    @Inject
    public CacheHttpBot(@Named("httpUrl") String url, WireRegister wireRegister) {
      super(url);
      this.wireRegister = wireRegister;
      this.url = url;
      if (this.wireRegister.hasContentFor(url)) {
        TestHelper.assumeReachable(url); // TODO this is an error
      }
    }

    @Override
    public synchronized String performAction(ContentProcessable a) {
      log.debug("{}", a);
      String response = wireRegister.getResponse(url, a);
      if (response != null) {
        return response;
      } else {
        return wireRegister.putResponse(url, a, super.performAction(a));
      }
    }
  }

  public static String getWikiUser(Version v) {
    v.getClass();
    return "Admin";
  }

  public static String getWikiPass(Version v) {
    v.getClass();
    return "nimdA";
  }

  public static String getWikiUrl(Version v) {
    String local = LiveTestFather.getValue("localwikihost");
    String version = "mw-" + v.getNumber().replace(".", "-");
    return "http://" + local + "/" + version + "/index.php";
  }
}
