package net.sourceforge.jwbf.core.bots;

import static com.google.common.net.HttpHeaders.ACCEPT_ENCODING;
import static com.google.common.net.HttpHeaders.CONNECTION;
import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static net.sourceforge.jwbf.JettyServer.entry;
import static net.sourceforge.jwbf.JettyServer.headerMapHandler;
import static net.sourceforge.jwbf.JettyServer.textHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.junit.After;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.core.actions.HttpActionClient;

public class HttpBotTest {

  private static JettyServer server;

  private static void startServerWith(ContextHandler userAgentHandler) {
    server = new JettyServer();
    server.setHandler(userAgentHandler);
    server.startSilent();
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
    String url = server.getTestUrl();
    HttpActionClient client = HttpActionClient.of(url);

    // WHEN
    String result = HttpBot.getPage(client);

    // THEN
    assertEquals(userAgentHeaderOf("Unknown/Unknown "), result);
  }

  @Test
  public final void testGetPage_UserAgent_any() {
    // GIVEN
    startServerWith(headerMapHandler());
    String url = server.getTestUrl();
    String userAgent = "myUserAgent";
    String userAgentVersion = "1.0";

    HttpActionClient client =
        HttpActionClient.builder() //
            .withUrl(url) //
            .withUserAgent(userAgent, userAgentVersion) //
            .build();

    // WHEN
    String result = HttpBot.getPage(client);

    // THEN

    assertEquals(userAgentHeaderOf(userAgent, userAgentVersion), result);
  }

  @Test
  public void testGetPage() {
    // GIVEN
    String expected = "test\n";
    startServerWith(textHandler(expected));
    String url = server.getTestUrl();

    // WHEN
    String page = HttpBot.getPage(url);

    // THEN
    assertEquals(expected, page);
  }

  private String userAgentHeaderOf(String userAgentName, String userAgentVersion) {
    String userAgentString = userAgentName + "/" + userAgentVersion + " ";
    return userAgentHeaderOf(userAgentString);
  }

  private String userAgentHeaderOf(String userAgentString) {
    ImmutableList<String> expected =
        ImmutableList.<String>builder()
            .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
            .add(entry(CONNECTION, "keep-alive")) //
            .add(entry(HOST, "localhost:????")) //
            .add(
                entry(
                    USER_AGENT,
                    userAgentString
                        + "JWBF/"
                        + JWBF.getVersion(HttpActionClient.class)
                        + " "
                        + HttpActionClient.httpClientVersion())) //
            .build();

    return Joiner.on("\n").join(expected) + "\n";
  }

  @After
  public void afterClass() {
    server.stopSilent();
  }
}
