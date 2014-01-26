package net.sourceforge.jwbf.mediawiki;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.ReturningText;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.mockito.Mockito;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class BotFactory {

  private BotFactory() {
    // do nothing
  }

  private static Injector masterInjector = Guice.createInjector(new AbstractModule() {

    @Override
    protected void configure() {
      bind(WireRegister.class);

    }
  });

  public static MediaWikiBot getMediaWikiBot(final Version v, final boolean login) {

    Injector injector = getBotInjector(v, login);
    return injector.getInstance(MediaWikiBot.class);
  }

  public static Injector getBotInjector(Version v, boolean login) {
    final String wikiUrl = getWikiUrl(v);
    TestHelper.assumeReachable(wikiUrl);
    Injector injector = masterInjector.createChildInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(CacheActionClient.class).toInstance(
            Mockito.spy(new CacheActionClient(wikiUrl, new WireRegister())));
        bind(HttpBot.class).to(CacheHttpBot.class);
        bind(MediaWikiBot.class).asEagerSingleton();
      }
    });
    MediaWikiBot bot = injector.getInstance(MediaWikiBot.class);
    if (login) {
      bot.login(getWikiUser(v), getWikiPass(v));
    }
    return injector;
  }

  @Slf4j
  @Singleton
  private static class CacheHttpBot extends HttpBot {

    @Inject
    public CacheHttpBot(CacheActionClient actionClient) {
      super(actionClient);
    }
  }

  @Slf4j
  public static class CacheActionClient extends HttpActionClient {

    public CacheActionClient(String url, WireRegister wireRegister) {
      super(HttpActionClient.newURL(url));
      this.wireRegister = wireRegister;
    }

    private final WireRegister wireRegister;

    public CacheActionClient(URL url, WireRegister wireRegister) {
      super(url);
      this.wireRegister = wireRegister;
      if (this.wireRegister.hasContentFor(url.toString())) {
        TestHelper.assumeReachable(url); // TODO this is an error
      }
    }

    @Override
    protected String processAction(HttpAction httpAction, ReturningText answerParser) {
      log.debug("{}", httpAction);
      String response = wireRegister.getResponse(httpAction);
      if (response != null) {
        return response;
      } else {
        return wireRegister.putResponse(httpAction, super.processAction(httpAction, answerParser));
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
    return "http://" + local + "/" + version + MediaWiki.URL_INDEX;
  }
}
