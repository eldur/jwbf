package net.sourceforge.jwbf;

import com.github.dreamhead.moco.*;
import com.github.dreamhead.moco.matcher.CompositeRequestMatcher;
import com.github.dreamhead.moco.monitor.AbstractMonitor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractIntegTest {

  private Runner runner;
  private int port;
  @VisibleForTesting protected HttpServer server;

  @Before
  public void before() {
    server = Moco.httpServer(); // new IntegMonitor());
    runner = Runner.runner(server);
    runner.start();
    port = server.port();
  }

  protected String host() {
    return "http://localhost:" + port + "/";
  }

  @After
  public void tearDown() {
    runner.stop();
  }

  public static CompositeRequestMatcher onlyOnce(RequestMatcher matcher) {
    return only(ImmutableList.of(matcher), 1);
  }

  public static CompositeRequestMatcher only(Iterable<RequestMatcher> matchers, final int times) {
    return new OnlyMatcher(matchers, times);
  }

  static class OnlyMatcher extends CompositeRequestMatcher {
    private Iterable<RequestMatcher> matchers;
    private final int times;
    private final AtomicInteger countdown;

    public OnlyMatcher(Iterable<RequestMatcher> matchers, int times) {
      super(matchers);
      this.matchers = matchers;
      this.times = times;
      countdown = new AtomicInteger(times);
    }

    @Override
    protected RequestMatcher newMatcher(Iterable<RequestMatcher> iterable) {
      return new OnlyMatcher(iterable, times);
    }

    @Override
    public boolean match(Request request) {
      if (countdown.get() != 0) {
        for (RequestMatcher matcher : matchers) {
          if (!matcher.match(request)) {
            return false;
          }
        }
        countdown.decrementAndGet();
        return true;
      } else {
        return false;
      }
    }

    @Override
    protected boolean doMatch(Request request, Iterable<RequestMatcher> iterable) {
      return this.matchers.equals(iterable);
    }
  }

  private static class IntegMonitor extends AbstractMonitor {

    private static Logger log = LoggerFactory.getLogger(IntegMonitor.class);

    @Override
    public void onMessageArrived(Request request) {
      if (request instanceof HttpRequest) {
        HttpRequest httpRequest = (HttpRequest) request;
        log.info(httpRequest.getUri() + " " + httpRequest.getQueries().toString());
      } else {
        log.warn("unknown type: " + request.getClass().getCanonicalName());
      }
    }
  }
}
