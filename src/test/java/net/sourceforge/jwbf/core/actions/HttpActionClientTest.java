package net.sourceforge.jwbf.core.actions;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.google.common.io.ByteSource;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.JettyServer;
import net.sourceforge.jwbf.Logging;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.VersionInfo;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.net.HttpHeaders.*;
import static net.sourceforge.jwbf.JettyServer.entry;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HttpActionClientTest {

  private static final Function<String, Long> TO_LONG =
      new Function<String, Long>() {

        @Override
        public Long apply(String input) {
          return Long.valueOf(input.trim());
        }
      };
  private HttpActionClient testee;

  public static final ReturningTextProcessor MOCK_HANDLER =
      new ReturningTextProcessor() {
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
  public void testHostUrlType() {
    // GIVEN
    testee = HttpActionClient.of(JWBF.newURL("http://localhost/"));

    // WHEN/THEN
    assertEquals("http://localhost", testee.getHostUrl());
    assertEquals("http://localhost/", testee.getUrl());
  }

  @Test
  public void testHostUrlTypeConstructor() {
    // GIVEN
    testee = new HttpActionClient(JWBF.newURL("http://localhost/"));

    // WHEN/THEN
    assertEquals("http://localhost", testee.getHostUrl());
    assertEquals("http://localhost/", testee.getUrl());
  }

  @Test
  public void testHostUrlWithApacheConstructor() {
    // GIVEN
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    URL url = JWBF.newURL("http://localhost/");
    testee = new HttpActionClient(httpClientBuilder, url);

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
      String utf8RawData =
          "\uCE20 \u1173 \u05E4\u05E2\u05D9\u05DC\u05D5\u05EA \u05D4\u05D1\u05D9\u05E0"
              + "\u05D0\u00E1\u00DF\u00E7\u0915\u0916\u0940\u570B\u969B\u0255\u0255k\u0255o"
              + "\u02D0\u0250\u032Fe\u02D0a\u0255\u0250\u0251\u0252\u00E6\u0251\u0303\u0255"
              + "\u028Cb\u0253\u0299\u03B2c\u0255\u00E7\u0255\u0255\u00E7\u0255\u0254\u0254"
              + "\u0255\u0255\u00F6\u00E4\u00FC\u200B";
      assertEquals(utf8RawData, utf8Data);
      Post post =
          RequestBuilder.of(url) //
              .param("b", "c") //
              .postParam("a", utf8Data) //
              .buildPost() //
          ;

      // WHEN
      String result = testee.post(post);

      // THEN
      ImmutableList<String> expected =
          ImmutableList.<String>builder() //
              .add("b=c") //
              .addAll(multipartOf("a", utf8RawData)) //
              .add("") //
              .build();

      GAssert.assertEquals(expected, GAssert.toList(result));

    } finally {
      server.stopSilent();
    }
  }

  @Test
  public void testParameterArrays() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.echoHandler());
      server.startSilent();
      String url = "http://localhost:" + server.getPort();
      testee = HttpActionClient.of(url);
      Post post =
          RequestBuilder.of(url) //
              .param("b", "c") //
              .param("b", "e")
              .postParam("b", "c")
              .postParam("b", "e")
              .buildPost() //
              .postParam("a", "b");

      // WHEN
      String result = testee.post(post);

      // THEN
      ImmutableList<String> expected =
          ImmutableList.<String>builder()
              .add("b=c&b=e") // b = [c, e]
              .addAll(multipartOf("b", "c")) //
              .addAll(multipartOf("b", "e")) //
              .addAll(multipartOf("a", "b")) //
              .add("") //
              .build();

      GAssert.assertEquals(expected, GAssert.toList(result));

    } finally {
      server.stopSilent();
    }
  }

  private List<String> multipartOf(String key, String value) {
    return ImmutableList.<String>builder() //
        .add("Content-Disposition: form-data; name=\"" + key + "\"") //
        .add("Content-Type: */*; charset=UTF-8") //
        .add("Content-Transfer-Encoding: 8bit") //
        .add("") //
        .add(value) //
        .build();
  }

  @Test
  public void testGet_headers() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.headerMapHandler());
      server.startSilent();
      String url = server.getTestUrl();
      CloseableHttpClient httpClient = HttpClientBuilder.create().build();
      testee =
          HttpActionClient.builder() //
              .withClient(httpClient) //
              .withUrl(url) //
              .build();

      // WHEN
      String result = testee.get(new Get(url));

      // THEN
      ImmutableList<String> expected =
          ImmutableList.<String>builder() //
              .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
              .add(entry(CONNECTION, "keep-alive")) //
              .add(entry(HOST, "localhost:????")) //
              .add(entry(USER_AGENT, apacheHttpClientVersion())) //
              .add("") //
              .build();

      assertEquals(Joiner.on("\n").join(expected), result);

    } finally {
      server.stopSilent();
    }
  }

  private String userAgentString(String userAgentString) {
    return userAgentString + "JWBF/Version_unknown " + apacheHttpClientVersion();
  }

  private String apacheHttpClientVersion() {
    return VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client",
            HttpClientBuilder.class);
  }

  @Test
  public void testGet_headers_customUserAgent() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.headerMapHandler());
      server.startSilent();
      String url = server.getTestUrl();
      testee =
          newVersionMockBuilder() //
              .withUserAgent("āTeštBot", "ač43e3a", "User:WikipediāUserId") //
              .withUrl(url) //
              .build();

      // WHEN
      String result = testee.get(new Get(url));

      // THEN
      ImmutableList<String> expected =
          ImmutableList.<String>builder() //
              .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
              .add(entry(CONNECTION, "keep-alive")) //
              .add(entry(HOST, "localhost:????")) //
              .add(
                  entry(
                      USER_AGENT, userAgentString("?Te?tBot/a?43e3a (User:Wikipedi?UserId) "))) //
              .add("") //
              .build();

      assertEquals(Joiner.on("\n").join(expected), result);

    } finally {
      server.stopSilent();
    }
  }

  @Test
  public void testUserAgentNameNull() {
    try {
      // GIVEN / WHEN
      HttpActionClient.builder().withUserAgent(null, "", "");
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("User-Agent name must not be null", e.getMessage());
    }
  }

  @Test
  public void testUserAgentVersionNull() {
    try {
      // GIVEN / WHEN
      HttpActionClient.builder().withUserAgent("", null, "");
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("User-Agent version must not be null", e.getMessage());
    }
  }

  @Test
  public void testUserAgentCommentNull() {
    try {
      // GIVEN / WHEN
      HttpActionClient.builder().withUserAgent("", "", null);
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("User-Agent comment must not be null", e.getMessage());
    }
  }

  @Test
  public void testUserAgentJwbf() {
    // GIVEN / WHEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    newVersionMockBuilder() //
        .withUrl("http://example.org") //
        .build();

    // THEN
    GAssert.assertEquals(ImmutableList.<String>of(), logLinesSupplier.get());
  }

  @Test
  public void testUserAgentString() {
    // GIVEN / WHEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    List<HttpActionClient.UserAgentPart> parts =
        HttpActionClient.builder() //
            .withUserAgent("test", "1.0", "written by User:Testuser - testuser@example.org") //
            .userAgentParts;

    // THEN
    assertAgentPart("test", "1.0", "written by User:Testuser - testuser@example.org", parts);
    GAssert.assertEquals(ImmutableList.<String>of(), logLinesSupplier.get());
  }

  @Test
  public void testUserAgentString_encodingLogging() {
    // GIVEN / WHEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    List<HttpActionClient.UserAgentPart> parts =
        HttpActionClient.builder() //
            .withUserAgent("āTeštBot", "ač43e3a", "User:WikipediāUserId") //
            .userAgentParts;

    // THEN
    assertAgentPart("?Te?tBot", "a?43e3a", "User:Wikipedi?UserId", parts);
    GAssert.assertEquals(
        ImmutableList.<String>of(
            "[WARN] \"āTeštBot\" was encoded to \"?Te?tBot\"; because only iso8859 is supported",
            "[WARN] \"ač43e3a\" was encoded to \"a?43e3a\"; because only iso8859 is supported",
            "[WARN] \"User:WikipediāUserId\" was encoded to \"User:Wikipedi?UserId\"; because only "
                + "iso8859 is supported"),
        logLinesSupplier.get());
  }

  @Test
  public void testUserAgentString_whitespaceLogging() {
    // GIVEN / WHEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    List<HttpActionClient.UserAgentPart> parts =
        HttpActionClient.builder() //
            .withUserAgent(" name\r //with ", " version/\n\n with ", " comment/of (me) ") //
            .userAgentParts;

    // THEN
    assertAgentPart("name_with", "version_with", "comment/of me", parts);
    GAssert.assertEquals(
        ImmutableList.<String>of(
            "[WARN] \" name\\r //with \" was changed to \"name_with\"; because of User-Agent"
                + " name/version rules",
            "[WARN] \" version/\\n\\n with \" was changed to \"version_with\";"
                + " because of User-Agent"
                + " name/version rules",
            "[WARN] \" comment/of (me) \" was changed to \"comment/of me\"; because of User-Agent"
                + " comment rules"),
        logLinesSupplier.get());
  }

  @Test
  public void testUserAgentString_emptyLogging() {
    // GIVEN / WHEN
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();
    List<HttpActionClient.UserAgentPart> parts =
        HttpActionClient.builder() //
            .withUserAgent(" \t ", " \t ", " \n ") //
            .userAgentParts;

    // THEN
    assertAgentPart("Unknown", "Unknown", "", parts);
    GAssert.assertEquals(
        ImmutableList.<String>of(
            "[WARN] \" \\t \" was changed to \"Unknown\"; because of User-Agent name/version rules",
            "[WARN] \" \\t \" was changed to \"Unknown\"; because of User-Agent name/version rules",
            "[WARN] \" \\n \" was changed to \"\"; because of User-Agent comment rules"),
        logLinesSupplier.get());
  }

  private static void assertAgentPart(
      String expectedName,
      String expectedVersion,
      String expectedComment,
      List<HttpActionClient.UserAgentPart> actualParts) {
    HttpActionClient.UserAgentPart onlyElement = Iterables.getOnlyElement(actualParts);
    assertEquals(expectedName, onlyElement.name);
    assertEquals(expectedVersion, onlyElement.version);
    assertEquals(expectedComment, onlyElement.comment);
  }

  @Test
  public void testMissingUseragent() {
    Supplier<ImmutableList<String>> logLinesSupplier = Logging.newLogLinesSupplier();

    // GIVEN / WHEN
    HttpActionClient.builder() //
        .withClient(HttpClientBuilder.create().build()) //
        .withUrl("http://localhost/") //
        .build();

    // THEN
    GAssert.assertEquals(
        ImmutableList.of("[WARN] a User-Agent must be set in your client"), logLinesSupplier.get());
  }

  @Test
  public void testPostParameters() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.headerMapHandler());
      server.startSilent();

      Post post = RequestBuilder.of("/").buildPost();

      // WHEN
      HttpActionClient hac =
          newVersionMockBuilder()
              .withUrl(server.getTestUrl()) //
              .build();

      ResponseHandler<String> a =
          ContentProcessableBuilder //
              .create(hac) //
              .withActions(post) //
              .build();

      String result = Iterables.getOnlyElement(a.get());

      // THEN
      ImmutableList<String> expected =
          ImmutableList.<String>builder()
              .add(entry(ACCEPT_ENCODING, "gzip,deflate")) //
              .add(entry(CONNECTION, "keep-alive")) //
              .add(entry(CONTENT_LENGTH, "???")) //
              .add(entry(CONTENT_TYPE, "multipart/form-data; boundary=????")) //
              .add(entry(HOST, "localhost:????")) //
              .add(entry(USER_AGENT, userAgentString("Unknown/Unknown "))) //
              .add("") //
              .build();

      assertEquals(Joiner.on("\n").join(expected), result);

    } finally {
      server.stopSilent();
    }
  }

  private HttpActionClient.Builder newVersionMockBuilder() {
    return new HttpActionClient.Builder() {
      @Override
      String getJwbfVersion() {
        return JWBF.VERSION_FALLBACK_VALUE;
      }
    };
  }

  @Test
  public void testDebug() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");
    HttpAction action = new Get("a");
    HttpUriRequest request = mock(HttpUriRequest.class);
    when(request.getURI()).thenReturn(JWBF.toUri("http://localhost/wiki/api.php"));

    // WHEN
    ImmutableList<String> result =
        ImmutableList.copyOf((String[]) testee.debug(request, action, MOCK_HANDLER));

    // THEN
    ImmutableList<String> expected =
        ImmutableList.<String>builder() //
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

      HttpActionClient hac =
          HttpActionClient.builder() //
              .withUrl(server.getTestUrl()) //
              .withRequestsPerUnit(2, TimeUnit.SECONDS) //
              .build() //
          ;

      ResponseHandler<String> a =
          ContentProcessableBuilder //
              .create(hac) //
              .withActions(get, get, get, get, get, get) //
              .build();

      // WHEN
      ImmutableList<String> result = a.get();

      Iterable<Long> ints = Iterables.transform(result, TO_LONG);
      ImmutableList<Long> deltas = toRanges(ints);

      // THEN
      ImmutableList<Range<Long>> expected =
          ImmutableList.<Range<Long>>builder() //
              .add(Range.closed(0L, 600L)) //
              .add(Range.closed(0L, 600L)) //
              .add(Range.closed(0L, 600L)) //
              .add(Range.closed(400L, 900L)) //
              .add(Range.closed(400L, 900L)) //
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

  @Test
  public void testWriteToString() throws IOException {
    // GIVEN
    String text = "a";

    // WHEN
    String result = whenWriteToString(text);

    // THEN
    assertEquals("a\n", result);
  }

  @Test
  public void testWriteToStringMultiline() throws IOException {
    // GIVEN
    String text = "a\r\nb";

    // WHEN
    String result = whenWriteToString(text);

    // THEN
    assertEquals("a\nb\n", result);
  }

  private String whenWriteToString(String text) throws IOException {
    testee = HttpActionClient.of("http://localhost/");
    HttpAction action = mock(HttpAction.class);
    when(action.getCharset()).thenReturn("UTF-8");
    HttpResponse response = mock(HttpResponse.class);
    HttpEntity httpEntity = mock(HttpEntity.class);
    InputStream inputStream = ByteSource.wrap(text.getBytes()).openStream();
    when(httpEntity.getContent()).thenReturn(inputStream);
    when(response.getEntity()).thenReturn(httpEntity);

    return testee.writeToString(action, response);
  }

  @Test
  public void testWriteToString_withException() throws IOException {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");
    HttpAction action = mock(HttpAction.class);
    when(action.getCharset()).thenReturn("UTF-8");
    HttpResponse response = mock(HttpResponse.class);
    HttpEntity httpEntity = mock(HttpEntity.class);

    when(httpEntity.getContent()).thenThrow(IOException.class);
    when(response.getEntity()).thenReturn(httpEntity);

    try {
      // WHEN
      testee.writeToString(action, response);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      assertEquals("java.io.IOException", e.getMessage());
    }
  }

  @Test
  public void testProcessAction() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");

    try {
      // WHEN
      HttpAction action = mock(HttpAction.class);
      testee.processAction(action, null);
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("httpAction should be GET or POST", e.getMessage());
    }
  }

  @Test
  public void testConsume() throws IOException {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");
    HttpResponse response = mock(HttpResponse.class);
    HttpEntity httpEntity = mock(HttpEntity.class);
    when(httpEntity.getContent()).thenThrow(IOException.class);
    when(response.getEntity()).thenReturn(httpEntity);

    try {
      // WHEN
      testee.consume(response);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      assertEquals("java.io.IOException", e.getMessage());
    }
  }

  @Test
  public void testExecute() throws IOException {
    // GIVEN
    HttpClient failClient = mock(HttpClient.class);
    when(failClient.execute(Mockito.isA(HttpUriRequest.class))).thenThrow(IOException.class);
    testee =
        HttpActionClient.builder()
            .withUrl("http://localhost/") //
            .withClient(failClient)
            .build();
    HttpRequestBase requestBase = mock(HttpRequestBase.class);

    try {
      // WHEN
      testee.execute(requestBase);
      fail();
    } catch (IllegalStateException e) {
      // THEN
      assertEquals("java.io.IOException", e.getMessage());
    }
  }

  @Test
  public void testApplyToEntityBuilder_filterNullElements() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");

    String key = "a";
    Collection<Object> values = new ArrayList<>();
    values.add(null);
    Charset charset = Charsets.UTF_8;
    MultipartEntityBuilder builder = mock(MultipartEntityBuilder.class);

    // WHEN
    testee.applyToEntityBuilder(key, values, charset, builder);

    // THEN
    verifyNoMoreInteractions(builder);
  }

  @Test
  public void testApplyToEntityBuilder_withFile() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");

    String key = "a";
    File file = new File(".");
    Collection<Object> values = ImmutableList.<Object>of(file);
    Charset charset = Charsets.UTF_8;
    MultipartEntityBuilder builder = mock(MultipartEntityBuilder.class);

    // WHEN
    testee.applyToEntityBuilder(key, values, charset, builder);

    // THEN
    verify(builder).addBinaryBody(key, file);
  }

  @Test
  public void testApplyToEntityBuilder_fail() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");

    String key = "a";
    Collection<Object> values = ImmutableList.<Object>of(new Object());
    Charset charset = Charsets.UTF_8;
    MultipartEntityBuilder builder = mock(MultipartEntityBuilder.class);

    // WHEN
    try {
      testee.applyToEntityBuilder(key, values, charset, builder);
      fail();
    } catch (UnsupportedOperationException e) {
      // THEN
      assertEquals(
          "No Handler found for java.lang.Object. Only String or File is accepted, "
              + "because http parameters knows no other types.",
          e.getMessage());
    }
  }

  @Test
  public void testPerformAction() {
    JettyServer server = new JettyServer();
    try {
      // GIVEN
      server.setHandler(JettyServer.dateHandler());
      server.startSilent();
      testee = HttpActionClient.of(server.getTestUrl());
      ActionHandler actionHandler = mock(ActionHandler.class);
      when(actionHandler.hasMoreActions()).thenReturn(Boolean.TRUE, Boolean.FALSE);
      Get get = new RequestBuilder("/").buildGet();
      when(actionHandler.popAction()).thenReturn(get);

      // WHEN
      testee.performAction(actionHandler);

      // THEN
      Mockito.verify(actionHandler)
          .processReturningText(Mockito.isA(String.class), Mockito.eq(get));
    } finally {
      server.stopSilent();
    }
  }
}
