package net.sourceforge.jwbf.core.actions;

import static com.google.common.net.HttpHeaders.ACCEPT_ENCODING;
import static com.google.common.net.HttpHeaders.CONNECTION;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static net.sourceforge.jwbf.JettyServer.entry;
import static org.junit.Assert.assertEquals;
import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.core.RequestBuilder;

import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

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
      server.setHandler(JettyServer.headerMapHandler());
      server.startSilent();
      String url = server.getTestUrl();
      testee = HttpActionClient.builder() //
          .withClient(HttpClientBuilder.create().build()) //
          .withUrl(url) //
          .build();

      // WHEN
      byte[] bs = testee.get(new Get(url));

      // THEN
      ImmutableList<String> expected = ImmutableList.<String> builder()
          .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
          .add(entry(CONNECTION, "keep-alive")) //
          .add(entry(HOST, "localhost:????")) //
          .add(entry(USER_AGENT, "Apache-HttpClient/4.3.2 (java 1.5)")) //
          .build();

      assertEquals(Joiner.on("\n").join(expected), new String(bs).trim());

    } finally {
      server.stopSilent();
    }
  }

  @Test
  public void testPostParameters() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.headerMapHandler());
      server.startSilent();

      Post post = RequestBuilder.of("/").buildPost();

      HttpActionClient hac = HttpActionClient.builder() //
          .withUrl(server.getTestUrl()) //
          .withUserAgent("none") //
          .build() //
      ;
      ResponseHandler<String> a = ContentProcessableBuilder //
          .create(hac) //
          .withActions(post) //
          .build();

      // WHEN
      String result = a.get().trim();

      // THEN
      ImmutableList<String> expected = ImmutableList.<String> builder()
          .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
          .add(entry(CONNECTION, "keep-alive")) //
          .add(entry(CONTENT_LENGTH, "???")) //
          .add(entry(CONTENT_TYPE, "multipart/form-data; boundary=????")) //
          .add(entry(HOST, "localhost:????")) //
          .add(entry(USER_AGENT, "none")) //
          .build();

      assertEquals(Joiner.on("\n").join(expected), result);

    } finally {
      server.stopSilent();
    }
  }

}
