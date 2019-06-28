package net.sourceforge.jwbf;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.Runner;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestHelperTest {

  @Test
  public void testOff() {
    String url = "http://localhost:" + TestHelper.getFreePort();
    try {
      TestHelper.assumeReachable(url);
    } catch (AssumptionViolatedException e) {
      throw e;
    }
    fail(url + " is reachable");
  }

  @Test
  public void testOn() {
    HttpServer server = Moco.httpServer();
    server.request(RequestMatcher.ANY_REQUEST_MATCHER).response("fine");
    Runner runner = Runner.runner(server);
    runner.start();
    String url = "http://localhost:" + server.port();
    try {
      TestHelper.assumeReachable(url);
    } catch (AssumptionViolatedException e) {
      runner.stop();
      fail(url + " is not reachable");
      throw e;
    }
    runner.stop();
  }

  @Test
  public void testGetRandom() {

    // GIVEN
    Random random = mock(Random.class);
    when(random.nextInt(79)).thenReturn(42, 42, 42, 43, 44);

    // WHEN
    String result = TestHelper.getRandom(3, 48, 126, random);

    // THEN
    assertEquals("Z[\\", result);
  }
}
