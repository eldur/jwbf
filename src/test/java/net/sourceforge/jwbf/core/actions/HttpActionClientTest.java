package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;
import net.sourceforge.jwbf.JettyServer;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class HttpActionClientTest {

  private HttpActionClient testee;

  @Test
  public void testHostUrl() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");

    // WHEN/THEN
    assertEquals("http://localhost", testee.getHostUrl());
    assertEquals("http://localhost/", testee.getUrl());
  }

  @Test
  public void testHostUrlWithPath() {
    // GIVEN
    testee = HttpActionClient.of("https://localhost/a/b.html?a=b");

    // WHEN/THEN
    assertEquals("https://localhost", testee.getHostUrl());
    assertEquals("https://localhost/a/b.html?a=b", testee.getUrl());
  }

  @Test
  public void testGet() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      String text = "test content\n";
      server.setHandler(JettyServer.textHandler(text));
      server.startSilent();
      String url = "http://localhost:" + server.getPort();
      testee = HttpActionClient.of(url);
      // WHEN
      byte[] bs = testee.get(new Get(url));

      // THEN
      assertEquals(text, new String(bs));

    } finally {
      server.stopSilent();
    }
  }

  @Test
  public void testGet_headers() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.userAgentHandler());
      server.startSilent();
      String url = "http://localhost:" + server.getPort();
      testee = HttpActionClient.builder() //
          .withClient(new DefaultHttpClient()) //
          .withUrl(url) //
          .build();
      // WHEN
      byte[] bs = testee.get(new Get(url));

      // THEN
      assertEquals("{Connection=[keep-alive], User-Agent=[Apache-HttpClient/4.3.2 (java 1.5)]}\n",
          new String(bs));

    } finally {
      server.stopSilent();
    }
  }

}
