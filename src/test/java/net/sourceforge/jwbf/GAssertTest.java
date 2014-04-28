package net.sourceforge.jwbf;

import static org.junit.Assert.assertEquals;

import org.junit.ComparisonFailure;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class GAssertTest {

  @Test
  public void testAssertEquals() {
    ImmutableList<String> a = ImmutableList.of("a");
    ImmutableList<String> b = ImmutableList.of("a");
    GAssert.assertEquals(a, b);
  }

  @Test
  public void testAssertEquals_fail() {
    ImmutableList<String> a = ImmutableList.of("a");
    ImmutableList<String> b = ImmutableList.of("b");
    try {
      GAssert.assertEquals(a, b);
    } catch (ComparisonFailure e) {
      assertEquals("expected:<[a]> but was:<[b]>", e.getMessage());
    }
  }

}
