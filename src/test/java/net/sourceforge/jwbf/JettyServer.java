package net.sourceforge.jwbf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps.EntryTransformer;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.net.HttpHeaders;

public class JettyServer extends Server {

  public JettyServer() {
    super(0);
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
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap
        .<String, String> builder();
    for (String name : Collections.list(request.getHeaderNames())) {
      for (String headerValue : Collections.list(request.getHeaders(name))) {
        builder.put(name, headerValue);
      }
    }
    return builder.orderKeysBy(Ordering.natural()).build();
  }

  static ImmutableMultimap<String, String> filterLocalhost(ImmutableMultimap<String, String> in) {
    EntryTransformer<String, String, String> transformer = new EntryTransformer<String, String, String>() {

      @Override
      public String transformEntry(String key, String value) {
        if (key.equals(HttpHeaders.HOST)) {
          return value.replaceAll("localhost:[0-9]+", "localhost:????");
        } else if (key.equals(HttpHeaders.CONTENT_TYPE)) {
          return value.replaceAll("(boundary=)(.*)", "$1????");
        } else if (key.equals(HttpHeaders.CONTENT_LENGTH)) {
          return "???";
        } else {
          return value;
        }
      }
    };
    return ImmutableListMultimap.<String, String> builder() //
        .putAll(Multimaps.transformEntries(in, transformer)) //
        .orderKeysBy(Ordering.natural()) //
        .build();
  }

  public static ContextHandler headerMapHandler() {
    return new ContextHandler() {
      @Override
      public void doHandle(String arg0, Request request, HttpServletRequest arg2,
          HttpServletResponse response) throws IOException, ServletException {

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
      public void doHandle(String arg0, Request request, HttpServletRequest arg2,
          HttpServletResponse response) throws IOException, ServletException {

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
}
