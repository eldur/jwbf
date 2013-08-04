package net.sourceforge.jwbf.core.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.bots.HttpBot;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpBotTest {

  private static int port;
  private static Server server;

  @BeforeClass
  public static void beforeClass() throws Exception {
    server = new Server(0);
    ContextHandler handler = new ContextHandler() {
      @Override
      public void doHandle(String arg0, Request request, HttpServletRequest arg2,
          HttpServletResponse response) throws IOException, ServletException {
        response.getWriter().print(request.getHeader("User-Agent"));
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
      }
    };
    server.setHandler(handler);
    server.start();
    port = ((NetworkConnector) server.getConnectors()[0]).getLocalPort();
  }

  @Test
  public void testInit() throws Exception {
    // GIVEN
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
  public final void testUserAgent() throws Exception {
    // GIVEN
    String url = "http://localhost:" + port + "/";
    HttpActionClient client = HttpActionClient.of(url);

    // WHEN
    String result = getTrimed(client);

    // THEN
    assertEquals("JWBF " + JWBF.getVersion(getClass()), result);
  }

  @Test
  public final void testUserAgent_any() throws Exception {
    // GIVEN
    String url = "http://localhost:" + port + "/";
    String userAgent = "test user agent";
    HttpActionClient client = HttpActionClient.builder() //
        .withUrl(url) //
        .withUserAgent(userAgent) //
        .build();

    // WHEN
    String result = getTrimed(client);

    // THEN
    assertEquals(userAgent, result);
  }

  private String getTrimed(HttpActionClient client) {
    return HttpBot.getPage(client).trim();
  }

  @AfterClass
  public static void afterClass() throws Exception {
    server.stop();
  }

}
