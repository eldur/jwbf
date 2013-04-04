package net.sourceforge.jwbf.core.live;

import static net.sourceforge.jwbf.mediawiki.LiveTestFather.getValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.core.bots.HttpBot;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.Assume;
import org.junit.Test;

public class HttpBotTest {

  @Test
  public void testConstr() throws Exception {
    String url = getValue("wikiMW1_13_url");
    Assume.assumeFalse(url.trim().isEmpty());
    HttpBot bot = new HttpBot(url) {
    };
    assertNotNull(bot);
    bot = new HttpBot(new URL(url)) {
    };
    assertNotNull(bot);
  }

  /**
   * Test if useragent ist jwbf.
   * 
   */
  @Test
  public final void testUserAgent() throws Exception {
    Server server = new Server(0);
    ContextHandler handler = new ContextHandler() {
      @Override
      public void doHandle(String arg0, Request request, HttpServletRequest arg2,
          HttpServletResponse response) throws IOException, ServletException {
        response.getWriter().println(request.getHeader("User-Agent"));
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
      }
    };
    server.setHandler(handler);
    try {
      server.start();
      int port = server.getConnectors()[0].getLocalPort();
      String url = "http://localhost:" + port + "/";
      HttpBot bot = new HttpBot(url);
      String result = bot.getPage(url);
      assertTrue("useragent should contain \"JWBF\"", result.contains("JWBF"));
      assertTrue("useragent should contain actual version",
          result.contains(JWBF.getVersion(getClass())));
    } finally {
      server.stop();
    }
  }

}
