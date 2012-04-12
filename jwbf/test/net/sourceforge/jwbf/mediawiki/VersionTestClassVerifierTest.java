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
  public static VersionTestClassVerifier veri = new VersionTestClassVerifier(
      Fake.class);

  private MediaWikiBot bot;

  @Rule
  public Verifier verSu = veri.getSuccessRegister(this);

  @Before
  public void before() {
    bot = Mockito.mock(MediaWikiBot.class);

  }

  @Test
  public void fakeTest9() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_09);
  }

  @Test()
  public void fakeTest10mayFail() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_10);

  }

  @Test
  public void fakeTest11() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_11);
  }

  @Test
  public void fakeTest12() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_12);
  }

  @Test
  public void fakeTest13() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_13);
  }

  @Test
  public void fakeTest14() {
    Mockito.when(bot.getVersion()).thenReturn(Version.MW1_14);
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

  @SupportedBy({ Version.MW1_09, Version.MW1_16, Version.MW1_10,
      Version.MW1_11, Version.MW1_12, Version.MW1_13, Version.MW1_14,
      Version.MW1_15, Version.MW1_16, Version.MW1_17 })
  private static class Fake {

  }

  public MediaWikiBot get() {
    return bot;
  }
}
