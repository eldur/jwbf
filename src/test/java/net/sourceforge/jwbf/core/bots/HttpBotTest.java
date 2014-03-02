package net.sourceforge.jwbf.core.bots;

import static net.sourceforge.jwbf.JettyServer.headerMapHandler;
import static net.sourceforge.jwbf.JettyServer.textHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.core.actions.HttpActionClient;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.After;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class HttpBotTest {

  private static int port;
  private static JettyServer server;

  private static void startServerWith(ContextHandler userAgentHandler) {
    server = new JettyServer();
    server.setHandler(userAgentHandler);
    try {
      server.start();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    port = server.getPort();
  }

  @Test
  public void testInit() throws MalformedURLException {
    // GIVEN
    startServerWith(headerMapHandler());
    String url = "http://192.0.2.1/";
    // WHEN
    HttpBot bot = new HttpBot(url);
    // GIVEN
    assertNotNull(bot);
    // WHEN
    bot = new HttpBot(new URL(url));
    // THEN
    assertNotNull(bot);
  }

  @Test
  public final void testGetPage_UserAgent() {
    // GIVEN
    startServerWith(headerMapHandler());
    String url = "http://localhost:" + port + "/";
    HttpActionClient client = HttpActionClient.of(url);

    // WHEN
    String result = HttpBot.getPage(client);

    // THEN
    assertEquals(userAgentHeaderOf("JWBF " + JWBF.getVersion(getClass())), result);
  }

  @Test
  public final void testGetPage_UserAgent_any() {
    // GIVEN
    startServerWith(headerMapHandler());
    String url = "http://localhost:" + port + "/";
    String userAgent = "my user agent";

    HttpActionClient client = HttpActionClient.builder() //
        .withUrl(url) //
        .withUserAgent(userAgent) //
        .build();

    // WHEN
    String result = HttpBot.getPage(client);

    // THEN

    assertEquals(userAgentHeaderOf(userAgent), result);
  }

  @Test
  public void testGetPage() {
    // GIVEN
    String expected = "test\n";
    startServerWith(textHandler(expected));
    String url = "http://localhost:" + port + "/";

    // WHEN
    String page = HttpBot.getPage(url);

    // THEN
    assertEquals(expected, page);
  }

  private String userAgentHeaderOf(String userAgent) {
    ImmutableList<String> of = ImmutableList.of( //
        "Accept-Encoding=[gzip,deflate]", //
        "Connection=[keep-alive]", //
        "Host=[localhost:????]", //
        "User-Agent=[" + userAgent + "]");

    return "{" + Joiner.on(", ").join(of) + "}\n";
  }

  @After
  public void afterClass() throws Exception {
    server.stop();
  }

}
