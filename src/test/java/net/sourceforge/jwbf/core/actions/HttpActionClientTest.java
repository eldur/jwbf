package net.sourceforge.jwbf.core.actions;

import static com.google.common.net.HttpHeaders.ACCEPT_ENCODING;
import static com.google.common.net.HttpHeaders.CONNECTION;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import static net.sourceforge.jwbf.JettyServer.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.core.RequestBuilder;

import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;

public class HttpActionClientTest {

  private static final Function<String, Long> TO_LONG = new Function<String, Long>() {

    @Override
    public Long apply(String input) {
      return Long.valueOf(input.trim());
    }
  };
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
      String result = Iterables.getOnlyElement(a.get()).trim();

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

  @Test
  public void testThrottler() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.dateHandler());
      server.startSilent();

      Get get = RequestBuilder.of("/").buildGet();

      HttpActionClient hac = HttpActionClient.builder() //
          .withUrl(server.getTestUrl()) //
          .withRequestsPerUnit(2, TimeUnit.SECONDS) //
          .build() //
      ;

      ResponseHandler<String> a = ContentProcessableBuilder //
          .create(hac) //
          .withActions(get, get, get, get, get, get) //
          .build();

      // WHEN
      ImmutableList<String> result = a.get();

      Iterable<Long> ints = Iterables.transform(result, TO_LONG);
      ImmutableList<Long> deltas = toRanges(ints);

      // THEN
      ImmutableList<Range<Long>> expected = ImmutableList.<Range<Long>> builder() //
          .add(Range.closed(0l, 600l)) //
          .add(Range.closed(0l, 600l)) //
          .add(Range.closed(0l, 600l)) //
          .add(Range.closed(400l, 900l)) //
          .add(Range.closed(400l, 900l)) //
          .build();

      int n = 0;
      for (Range<Long> range : expected) {
        int index = n++;
        Long value = deltas.get(index);
        assertTrue("range(" + index + "): " + range + " val: " + value, range.contains(value));
      }

    } finally {
      server.stopSilent();
    }
  }

  private ImmutableList<Long> toRanges(Iterable<Long> ints) {
    ImmutableList<Long> intList = ImmutableList.copyOf(ints);
    Builder<Long> builder = ImmutableList.<Long> builder();
    for (int i = 0; i < intList.size() - 1; i++) {
      Long a = intList.get(0 + i);
      Long b = intList.get(1 + i);
      Long delta = b - a;
      builder.add(delta);
    }
    return builder.build();
  }

}
