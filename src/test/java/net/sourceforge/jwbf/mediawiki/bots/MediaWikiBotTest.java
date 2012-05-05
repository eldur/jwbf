package net.sourceforge.jwbf.mediawiki.bots;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;

import org.junit.Test;
import org.mockito.Mockito;

public class MediaWikiBotTest {

  private MediaWikiBot bot;

  @Test
  public void testWithMockClient() {
    HttpActionClient client = mock(HttpActionClient.class);
    when(client.performAction(Mockito.any(GetVersion.class))).thenReturn("");
    bot = new MediaWikiBot(client);
    Version version = bot.getVersion();
    assertEquals(Version.UNKNOWN, version);
  }

  // TODO test all other methods with a mock client
}
