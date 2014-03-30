package net.sourceforge.jwbf;

import org.junit.Assert;

import com.google.common.collect.ImmutableList;

public class GAssert {

  public static void assertEquals(ImmutableList<?> expected, ImmutableList<?> actual) {
    try {
      Assert.assertEquals(expected, actual);
    } catch (AssertionError e) {
      Assert.assertEquals(expected.toString(), actual.toString());

      throw e;
    }
  }

  public static void assertStartsWith(String expected, String actual) {
    if (actual != null) {
      if (actual.length() > expected.length()) {
        Assert.assertEquals(expected, actual.substring(0, expected.length()));
      } else {
        Assert.assertEquals(expected, actual);
      }
    } else {
      Assert.assertEquals(expected, actual);
    }

  }

}
