package net.sourceforge.jwbf.mediawiki;

import javax.inject.Provider;

import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Verifier;
import org.mockito.Mockito;

public class VersionTestClassVerifierTest implements Provider<MediaWikiBot> {

  @ClassRule
  public static VersionTestClassVerifier veri = new VersionTestClassVerifier(Fake.class)
      .dontCheckAll();

  private MediaWikiBot bot;

  @Rule
  public Verifier verSu = veri.getSuccessRegister(this);

  @Before
  public void before() {
    bot = Mockito.mock(MediaWikiBot.class);

  }

  @Test
  public void fakeTest15() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_15);
  }

  @Test
  public void fakeTest16() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_16);
  }

  @Test
  public void fakeTest17() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_17);
  }

  @SupportedBy({ Version.MW1_16, Version.MW1_15, Version.MW1_16, Version.MW1_17 })
  private static class Fake {

  }

  public MediaWikiBot get() {
    return bot;
  }
}
