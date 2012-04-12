package net.sourceforge.jwbf;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

public class TestHelperTest {

  @Test
  public void testOff() {
    String url = "http://192.0.2.1/";
    try {
      TestHelper.assumeReachable(url);
    } catch (AssumptionViolatedException e) {
      throw e;
    }
    fail(url + " is reachable");
  }

  @Test
  public void testOn() {
    String url = "http://www.google.com/";
    try {
      TestHelper.assumeReachable(url);
    } catch (AssumptionViolatedException e) {
      fail(url + " is not reachable");
      throw e;
    }
  }

}
