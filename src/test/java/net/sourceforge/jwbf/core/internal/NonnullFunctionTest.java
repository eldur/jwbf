package net.sourceforge.jwbf.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.annotation.Nonnull;

import org.junit.Test;

import com.google.common.base.Function;

public class NonnullFunctionTest {

  @Test
  public void testApplyNonnull() throws Exception {

    // GIVEN
    final String result = "result";
    Function f =
        new NonnullFunction() {
          @Nonnull
          @Override
          protected Object applyNonnull(@Nonnull Object input) {
            return result;
          }
        };

    // WHEN
    Object actual = f.apply("in");

    // THEN
    assertEquals(result, actual);
  }

  @Test
  public void testApplyNonnull_input() throws Exception {

    // GIVEN
    Function f =
        new NonnullFunction() {
          @Nonnull
          @Override
          protected Object applyNonnull(@Nonnull Object input) {
            return "";
          }
        };

    // WHEN
    try {
      f.apply(null);
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("input must not be null", e.getMessage());
    }
  }

  @Test
  public void testApplyNonnull_result() throws Exception {

    // GIVEN
    Function f =
        new NonnullFunction() {
          @Nonnull
          @Override
          protected Object applyNonnull(@Nonnull Object input) {
            return null;
          }
        };

    // WHEN
    try {
      f.apply("");
      fail();
    } catch (NullPointerException e) {
      // THEN
      assertEquals("result must not be null", e.getMessage());
    }
  }
}
