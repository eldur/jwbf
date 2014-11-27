package net.sourceforge.jwbf.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CheckedTest {

  @Test
  public void testNonBlank() {

    // WHEN
    String result = Checked.nonBlank("a", "first");

    // THWN
    assertEquals("a", result);
  }

  @Test
  public void testNonBlank_null() {
    try {
      // GIVEN
      Checked.nonBlank(null, "first");
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("The argument 'first' must not be null or empty", e.getMessage());
    }
  }

  @Test
  public void testNonBlank_blank() {
    try {
      // GIVEN
      Checked.nonBlank("", "any");
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("The argument 'any' must not be null or empty", e.getMessage());
    }
  }
}
