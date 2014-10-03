package net.sourceforge.jwbf;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Runner.runner;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.github.dreamhead.moco.matcher.CompositeRequestMatcher;
import com.github.dreamhead.moco.monitor.AbstractMonitor;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractIntegTest {

  private Runner runner;
  private int port;
  protected HttpServer server;

  @Before
  public void before() {
    server = httpserver(); // new IntegMonitor());
    runner = runner(server);
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

    return new CompositeRequestMatcher(matchers) {
      private final AtomicInteger countdown = new AtomicInteger(times);

      @Override
      public boolean match(HttpRequest request) {
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
      public RequestMatcher apply(final MocoConfig config) {
        Iterable<RequestMatcher> appliedMatchers = applyToMatchers(config);
        if (appliedMatchers == this.matchers) {
          return this;
        }

        return new AndRequestMatcher(appliedMatchers);
      }

    };
  }

  private static class IntegMonitor extends AbstractMonitor {

    private static Logger log = LoggerFactory.getLogger(IntegMonitor.class);
    @Override
    public void onMessageArrived(HttpRequest request) {
      log.info(request.getUri() + " " + request.getQueries().toString());
    }
  }
}
