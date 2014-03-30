package net.sourceforge.jwbf.mediawiki.bots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.GetVersion;

import org.junit.Test;
import org.mockito.Mockito;

public class MediaWikiBotTest {

  private MediaWikiBot testee;

  @Test
  public void testInitWithBuilder() {
    // GIVEN
    String url = "http://localhost/";
    // WHEN
    testee = new MediaWikiBot(HttpActionClient.of(url));
    // THEN
    assertNotNull(testee);

  }

  // TODO test all other methods with a mock client

  @Test
  public void testGetVersion_fail() {
    // GIVEN
    HttpActionClient client = mock(HttpActionClient.class);
    when(client.performAction(Mockito.any(GetVersion.class))).thenThrow(
        new IllegalStateException("fail"));
    testee = new MediaWikiBot(client);

    try {
      // WHEN
      testee.getVersion();
      fail();
    } catch (IllegalStateException e) {
      // THEN
      assertEquals("fail", e.getMessage());
    }
  }

  @Test
  public void testGetVersion() {
    // GIVEN
    HttpActionClient client = mock(HttpActionClient.class);
    when(client.performAction(Mockito.any(GetVersion.class))).thenReturn("");
    testee = new MediaWikiBot(client);

    // WHEN
    Version version = testee.getVersion();

    // THEN
    assertEquals(Version.UNKNOWN, version);
  }
}
