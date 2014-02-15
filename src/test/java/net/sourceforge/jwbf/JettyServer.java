package net.sourceforge.jwbf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimaps;

public class JettyServer extends Server {

  private static final Predicate<String> SKIP_HOST_VALUE = new Predicate<String>() {

    @Override
    public boolean apply(String input) {
      if (input != null && input.matches("localhost:[0-9]+")) {
        return false;
      }
      return true;
    }
  };

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

  public static ImmutableMultimap<String, String> headersOf(Request request) {
    ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap
        .<String, String> builder();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      Enumeration<String> headersOfName = request.getHeaders(name);
      while (headersOfName.hasMoreElements()) {
        String headerValue = headersOfName.nextElement();
        builder.put(name, headerValue);
      }
    }
    return builder.build();
  }

  public static ImmutableMultimap<String, String> filterLocalhost(
      ImmutableMultimap<String, String> in) {
    return ImmutableListMultimap. //
        copyOf(Multimaps.filterValues(in, SKIP_HOST_VALUE));
  }

  public static ContextHandler userAgentHandler() {
    return new ContextHandler() {
      @Override
      public void doHandle(String arg0, Request request, HttpServletRequest arg2,
          HttpServletResponse response) throws IOException, ServletException {

        ImmutableMultimap<String, String> headerMap = JettyServer.headersOf(request);

        PrintWriter writer = response.getWriter();
        writer.print(JettyServer.filterLocalhost(headerMap));
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
}
