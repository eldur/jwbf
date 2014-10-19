package net.sourceforge.jwbf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

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
  public void testOffRetry() {
    for (int i = 0; i < 50; i++) {
      testOff();
      testOn();
    }
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
