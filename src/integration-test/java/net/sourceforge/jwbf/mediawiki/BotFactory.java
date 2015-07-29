package net.sourceforge.jwbf.mediawiki;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.concurrent.TimeUnit;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.ReturningTextProcessor;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    return getIntegMediaWikiBot(v, login);
  }

  public static MediaWikiBot getIntegMediaWikiBot(final Version v, final boolean login) {
    Injector injector = getBotInjector(v, login);
    return injector.getInstance(MediaWikiBot.class);
  }

  public static Injector getBotInjector(Version v, boolean login) {
    final String wikiUrl = getWikiUrl(v, LiveTestFather.getValue("localwikihost"));
    TestHelper.assumeReachable(wikiUrl);
    Injector injector = masterInjector.createChildInjector(new AbstractModule() {

      @Override
      protected void configure() {
        bind(CacheActionClient.class) //
            .toInstance(Mockito.spy(new CacheActionClient(wikiUrl, new WireRegister())));
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

  @Singleton
  private static class CacheHttpBot extends HttpBot {

    @Inject
    public CacheHttpBot(CacheActionClient actionClient) {
      super(actionClient);
    }
  }

  public static class CacheActionClient extends HttpActionClient {

    private static final Logger log = LoggerFactory.getLogger(CacheActionClient.class);

    public CacheActionClient(String url, WireRegister wireRegister) {
      super(JWBF.newURL(url));
      this.wireRegister = wireRegister;
    }

    private final WireRegister wireRegister;

    @Override
    protected String processAction(HttpAction httpAction, ReturningTextProcessor answerParser) {
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

  private static String getWikiUrl(Version v, String host) {
    String version = "mw-" + v.getNumber().replace(".", "-");
    return "http://" + host + "/" + version + MediaWiki.URL_INDEX;
  }

  public static MediaWikiBot newWikimediaBot(String liveUrl, Class<?> clazz) {

    String self = getUrlForWikimedia(clazz);
    HttpActionClient client = HttpActionClient.builder() //
        .withUrl(liveUrl) //
        .withUserAgent("JwbfIntegTest", "A", "could be found at " + self +
            " or at any fork of this project") //
        .withRequestsPerUnit(10, TimeUnit.MINUTES) //
        .build();
    return new MediaWikiBot(client);
  }

  static String getUrlForWikimedia(Class<?> clazz) {
    String prefix = "https://github.com/eldur/jwbf/blob/master/src/integration-test/java/";
    return prefix + clazz.getCanonicalName().replace('.', '/') + ".java";
  }

}
