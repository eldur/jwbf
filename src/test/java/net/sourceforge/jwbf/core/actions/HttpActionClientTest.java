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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class HttpActionClientTest {

  private static final Function<String, Long> TO_LONG = new Function<String, Long>() {

    @Override
    public Long apply(String input) {
      return Long.valueOf(input.trim());
    }
  };
  private HttpActionClient testee;

  public static final ReturningTextProcessor MOCK_HANDLER = new ReturningTextProcessor() {
    @Override
    public String processReturningText(String s, HttpAction action) {
      throw new UnsupportedOperationException();
    }
  };

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
      String bs = testee.get(new Get(url));

      // THEN
      assertEquals(text, bs);

    } finally {
      server.stopSilent();
    }
  }

  @Test
  public void testPostEncoding() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.echoHandler());
      server.startSilent();
      String url = "http://localhost:" + server.getPort();
      testee = HttpActionClient.of(url);
      String utf8Data = "츠 ᅳ פעילות הבינאáßçकखी國際ɕɕkɕoːɐ̯eːaɕɐɑɒæɑ̃ɕʌbɓʙβcɕçɕɕçɕɔɔɕɕöäü\u200B";
      String utf8RawData = "\uCE20 \u1173 \u05E4\u05E2\u05D9\u05DC\u05D5\u05EA \u05D4\u05D1\u05D9\u05E0\u05D0\u00E1\u00DF\u00E7\u0915\u0916\u0940\u570B\u969B\u0255\u0255k\u0255o\u02D0\u0250\u032Fe\u02D0a\u0255\u0250\u0251\u0252\u00E6\u0251\u0303\u0255\u028Cb\u0253\u0299\u03B2c\u0255\u00E7\u0255\u0255\u00E7\u0255\u0254\u0254\u0255\u0255\u00F6\u00E4\u00FC\u200B";
      assertEquals(utf8RawData, utf8Data);
      Post post = RequestBuilder.of(url) //
          .param("b", "c").buildPost() //
          .postParam("a", utf8Data);

      // WHEN
      String bs = testee.post(post);

      // THEN
      ImmutableList<String> result = ImmutableList.<String>builder()
          .add("b=c") //
          .add("Content-Disposition: form-data; name=\"a\"") //
          .add("Content-Type: */*; charset=UTF-8") //
          .add("Content-Transfer-Encoding: 8bit") //
          .add("") //
          .add(utf8RawData) //
          .add("")
          .build();

      GAssert.assertEquals(result, GAssert.toList(bs));

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
      String bs = testee.get(new Get(url));

      // THEN
      ImmutableList<String> expected = ImmutableList.<String>builder()
          .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
          .add(entry(CONNECTION, "keep-alive")) //
          .add(entry(HOST, "localhost:????")) //
          .add(entry(USER_AGENT, "Apache-HttpClient/4.3.3 (java 1.5)")) //
          .build();

      assertEquals(Joiner.on("\n").join(expected), bs.trim());

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
      ImmutableList<String> expected = ImmutableList.<String>builder()
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
  public void testDebug() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");
    HttpAction action = new Get("a");
    HttpUriRequest request = mock(HttpUriRequest.class);
    when(request.getURI()).thenReturn(JWBF.toUri("http://localhost/wiki/api.php"));

    // WHEN
    ImmutableList<String> result = ImmutableList.copyOf((String[]) testee.debug(request, action, MOCK_HANDLER));

    // THEN
    ImmutableList<String> expected = ImmutableList.<String>builder() //
        .add("(GET net.sourceforge.jwbf.core.actions.HttpActionClientTest$2)") //
        .add("http://localhost/wiki") //
        .add("a") //
        .build();
    GAssert.assertEquals(expected, result);
  }

  @Test
  public void testDebug_withMockAction() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");
    ReturningTextProcessor responseHandler = mock(ReturningTextProcessor.class);
    HttpAction action = mock(HttpAction.class);
    HttpUriRequest request = mock(HttpUriRequest.class);
    when(request.getURI()).thenReturn(JWBF.toUri("http://localhost/wiki/api.php"));
    try {
      // WHEN
      testee.debug(request, action, responseHandler);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      GAssert.assertStartsWith("unknown type: ", e.getMessage());
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
      ImmutableList<Range<Long>> expected = ImmutableList.<Range<Long>>builder() //
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
    Builder<Long> builder = ImmutableList.builder();
    for (int i = 0; i < intList.size() - 1; i++) {
      Long a = intList.get(i);
      Long b = intList.get(1 + i);
      Long delta = b - a;
      builder.add(delta);
    }
    return builder.build();
  }

}
