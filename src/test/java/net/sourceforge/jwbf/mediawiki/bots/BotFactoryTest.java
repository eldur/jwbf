package net.sourceforge.jwbf.mediawiki.bots;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.sourceforge.jwbf.core.bots.HttpBot;
import org.junit.Test;

public class BotFactoryTest {

  @Test
  public void testIoC() {
    // GIVEN
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(HttpBot.class).toInstance(new HttpBot("http://192.0.2.2/"));
      }
    });

    // WHEN
    MediaWikiBot instance = injector.getInstance(MediaWikiBot.class);

    // THEN
    instance.bot(); // no exception
  }
}
