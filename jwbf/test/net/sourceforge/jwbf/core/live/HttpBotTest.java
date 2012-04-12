package net.sourceforge.jwbf.core.live;

import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.sourceforge.jwbf.core.bots.HttpBot;

import org.junit.Before;
import org.junit.Test;

public class HttpBotTest {

  private HttpBot bot;

  @Before
  public void prepare() {
    bot = HttpBot.getInstance();
  }

  /**
   * Test write and read
   * 
   * @throws Exception
   *           a
   */
  @Test
  public final void findContent() throws Exception {
    URL u = new URL(getValue("wikiMW1_13_url"));
    String url = u.getProtocol() + "://" + u.getHost();
    String s = bot.getPage(url);
    assertTrue("content shuld be longer then one", s.length() > 1);
    byte[] b = bot.getBytes(url);
    String bs = new String(b);
    assertTrue("content shuld be longer then one", bs.length() > 1);
    assertEquals(s, bs);
    assertNotNull(bot.getClient());
    assertEquals(url, bot.getHostUrl());
  }

  @Test
  public void testConstr() throws Exception {
    HttpBot bot = new HttpBot(getValue("wikiMW1_13_url")) {
    };
    assertNotNull(bot);
    bot = new HttpBot(new URL(getValue("wikiMW1_13_url"))) {
    };
    assertNotNull(bot);
  }

}
