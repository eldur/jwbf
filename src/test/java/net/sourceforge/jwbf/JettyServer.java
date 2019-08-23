package net.sourceforge.jwbf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.io.CharStreams;
import com.google.common.net.HttpHeaders;

public class JettyServer extends Server implements AutoCloseable {

  public JettyServer() {
    super(0);
  }

  public JettyServer started(ContextHandler handler) {
    setHandler(handler);
    startSilent();
    return this;
  }

  public void startSilent() {
    try {
      super.start();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void stopSilent() {
    try {
      super.stop();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public int getPort() {
    if (isStarted()) {
      return ((NetworkConnector) getConnectors()[0]).getLocalPort();
    } else {
      throw new IllegalStateException("please start server");
    }
  }

  static ImmutableMultimap<String, String> headersOf(Request request) {
    ImmutableListMultimap.Builder<String, String> builder =
        ImmutableListMultimap.<String, String>builder();
    for (String name : Collections.list(request.getHeaderNames())) {
      for (String headerValue : Collections.list(request.getHeaders(name))) {
        builder.put(name, headerValue);
      }
    }
    return builder.orderKeysBy(Ordering.natural()).build();
  }

  static ImmutableMultimap<String, String> filterLocalhost(ImmutableMultimap<String, String> in) {
    EntryTransformer<String, String, String> transformer =
        new EntryTransformer<String, String, String>() {

          @Override
          public String transformEntry(String key, String value) {
            switch (key) {
              case HttpHeaders.HOST:
                return value.replaceAll("localhost:[0-9]+", "localhost:????");
              case HttpHeaders.CONTENT_TYPE:
                return value.replaceAll("(boundary=)(.*)", "$1????");
              case HttpHeaders.CONTENT_LENGTH:
                return "???";
              default:
                return value;
            }
          }
        };
    return ImmutableListMultimap.<String, String>builder() //
        .putAll(Multimaps.transformEntries(in, transformer)) //
        .orderKeysBy(Ordering.natural()) //
        .build();
  }

  public static ContextHandler headerMapHandler() {
    return new ContextHandler() {
      @Override
      public void doHandle(
          String arg0, Request request, HttpServletRequest arg2, HttpServletResponse response)
          throws IOException, ServletException {

        ImmutableMultimap<String, String> headerMap = JettyServer.headersOf(request);

        PrintWriter writer = response.getWriter();
        ImmutableMultimap<String, String> filtered = JettyServer.filterLocalhost(headerMap);
        // TODO write JSON
        writer.print(Joiner.on("\n").join(filtered.entries()));
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
      }
    };
  }

  public static ContextHandler textHandler(final String text) {
    return new ContextHandler() {
      @Override
      public void doHandle(
          String arg0, Request request, HttpServletRequest arg2, HttpServletResponse response)
          throws IOException, ServletException {

        PrintWriter writer = response.getWriter();
        writer.print(text);
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
      }
    };
  }

  public static String entry(String key, String value) {
    return key + "=" + value + "";
  }

  public String getTestUrl() {
    return "http://localhost:" + getPort() + "/";
  }

  @Override
  public void close() throws Exception {
    stopSilent();
  }

  @MultipartConfig
  private static class EchoHandler extends ContextHandler {

    private static final MultipartConfigElement MULTI_PART_CONFIG =
        new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    @Override
    public void doHandle(
        String arg0, Request request, HttpServletRequest req, HttpServletResponse response)
        throws IOException, ServletException {

      PrintWriter writer = response.getWriter();
      writer.print(request.getQueryString());
      Joiner joiner = Joiner.on("\n").useForNull("NULL");

      writer.println();
      // FIXME change multiparthandling
      if (false
          && request.getContentType() != null
          && request.getContentType().startsWith("multipart/form-data")) {
        req.setAttribute(Request.MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
        writer //
            .println(
            joiner.join(
                Iterables.transform(
                    req.getParts(),
                    new Function<Part, String>() {
                      @Nullable
                      @Override
                      public String apply(@Nullable Part input) {
                        return input.getName();
                      }
                    })));
      } else {
        List<String> lines = CharStreams.readLines(req.getReader());
        writer.print(
            joiner.join(
                Iterables.filter(
                    lines,
                    new Predicate<String>() {
                      @Override
                      public boolean apply(@Nullable String input) {
                        return !input.startsWith("--");
                      }
                    })));
      }
      response.setStatus(HttpServletResponse.SC_OK);
      request.setHandled(true);
    }
  };

  public static ContextHandler echoHandler() {
    return new EchoHandler();
  }

  public static ContextHandler dateHandler() {
    return new ContextHandler() {
      @Override
      public void doHandle(
          String arg0, Request request, HttpServletRequest req, HttpServletResponse response)
          throws IOException, ServletException {

        PrintWriter writer = response.getWriter();
        DateTime now = DateTime.now();

        writer.print(now.getMillis());
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
      }
    };
  }
}
