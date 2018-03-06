package net.sourceforge.jwbf.mediawiki.actions.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ApiExceptionTest {

  @Test
  public void testInit() {
    // GIVEN
    ApiException testee = new ApiException("a", "b");

    // WHEN / THEN
    assertEquals("API ERROR CODE: a VALUE: b", testee.getMessage());
  }
}
