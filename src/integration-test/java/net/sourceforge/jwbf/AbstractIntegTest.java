package net.sourceforge.jwbf;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Runner.runner;

import org.junit.After;
import org.junit.Before;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Runner;

public abstract class AbstractIntegTest {

  private Runner runner;
  private int port;
  protected HttpServer server;

  @Before
  public void before() {
    server = httpserver(); // Moco.log()
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
}
