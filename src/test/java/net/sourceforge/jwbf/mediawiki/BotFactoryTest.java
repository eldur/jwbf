package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.jwbf.mediawiki.live.auto.UserinfoIT;

public class BotFactoryTest {

  @Test
  public void testGetUrlForWikimedia() {
    // GIVEN / WHEN
    String url = BotFactory.getUrlForWikimedia(UserinfoIT.class);

    // THEN
    assertEquals(
        "https://github.com/eldur/jwbf/blob/master/src/integration-test/java/net"
            + "/sourceforge/jwbf/mediawiki/live/auto/UserinfoIT.java",
        url);
  }
}
