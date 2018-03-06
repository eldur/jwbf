package net.sourceforge.jwbf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.ComparisonFailure;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;

public class GAssertTest {

  @Test
  public void testAssertEquals_list() {
    // GIVEN
    ImmutableList<String> expected = ImmutableList.of("a");
    ImmutableList<String> actual = ImmutableList.of("a");

    // WHEN / THEN
    GAssert.assertEquals(expected, actual);
  }

  @Test
  public void testAssertEquals_list_null() {
    // GIVEN
    ImmutableList<String> expected = null;
    ImmutableList<String> actual = null;

    // WHEN / THEN
    GAssert.assertEquals(expected, actual);
  }

  @Test
  public void testAssertEquals_list_null_actual() {
    // GIVEN
    ImmutableList<String> expected = ImmutableList.of("a");
    ImmutableList<String> actual = null;

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (AssertionError e) {
      // THEN
      assertEquals("expected:<java.lang.String a> but was:<null>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_list_null_expected() {
    // GIVEN
    ImmutableList<String> expected = null;
    ImmutableList<String> actual = ImmutableList.of("a");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (AssertionError e) {
      // THEN
      assertEquals("expected:<null> but was:<java.lang.String a>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_list_fail() {
    // GIVEN
    ImmutableList<String> expected = ImmutableList.of("a");
    ImmutableList<String> actual = ImmutableList.of("b");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals("expected:<[a]> but was:<[b]>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_list_fail_2() {
    // GIVEN
    ImmutableList<String> expected = ImmutableList.of("a");
    ImmutableList<String> actual = ImmutableList.of("b", "c");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals("expected:<[a]> but was:<[b\nc]>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_listTypes_fail() {
    // GIVEN
    ImmutableList<String> expected = ImmutableList.of("a");
    ImmutableList<Integer> actual = ImmutableList.of(4);

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "expected:<java.lang.[String a]> but was:<java.lang.[Integer 4]>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_listTypes_fail_optical() {
    // GIVEN
    ImmutableList<String> expected = ImmutableList.of("4");
    ImmutableList<Integer> actual = ImmutableList.of(4);

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "expected:<java.lang.[String] 4> but was:<java.lang.[Integer] 4>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_map_mutable() {
    // GIVEN
    ImmutableMap<String, String> expected = ImmutableMap.of("a", "a");
    Map<String, String> actual = Maps.newHashMap();
    actual.put("a", "a");

    // WHEN / THEN
    GAssert.assertEquals(expected, actual);
  }

  @Test
  public void testAssertEquals_map() {
    // GIVEN
    ImmutableMap<String, String> expected = ImmutableMap.of("a", "a");
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "a");

    // WHEN / THEN
    GAssert.assertEquals(expected, actual);
  }

  @Test
  public void testAssertEquals_map_key_fail() {
    // GIVEN
    ImmutableMap<String, String> expected = ImmutableMap.of("a", "a");
    ImmutableMap<String, String> actual = ImmutableMap.of("b", "b");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals("expected:<[a=a]> but was:<[b=b]>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_map_key_type_fail() {
    // GIVEN
    ImmutableMap<Integer, String> expected = ImmutableMap.of(4, "a");
    ImmutableMap<String, String> actual = ImmutableMap.of("b", "b");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "expected:<{[4 [java.lang.Integer]}={a] [java.lang.String]}>"
              + " but was:<{[b [java.lang.String]}={b] [java.lang.String]}>",
          e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_map_value_fail() {
    // GIVEN
    ImmutableMap<String, String> expected = ImmutableMap.of("a", "a");
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "b");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals("expected:<a=[a]> but was:<a=[b]>", e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_map_value_types_fail() {
    // GIVEN
    ImmutableMap<String, Integer> expected = ImmutableMap.of("a", 4);
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "b");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "expected:<...java.lang.String]}={[4 [java.lang.Integer]]}> "
              + "but was:<...java.lang.String]}={[b [java.lang.String]]}>",
          e.getMessage());
    }
  }

  @Test
  public void testAssertEquals_multimap_value_types_fail() {
    // GIVEN
    ImmutableMultimap<String, Integer> expected = ImmutableMultimap.of("a", 4, "a", 5);
    ImmutableMultimap<String, String> actual = ImmutableMultimap.of("a", "b", "a", "c");

    try {
      // WHEN
      GAssert.assertEquals(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "expected:<...java.lang.String]}={[4 [java.lang.Integer]}\n"
              + "  {5 [java.lang.Integer]]}\n"
              + "> but was:<...java.lang.String]}={[b [java.lang.String]}\n"
              + "  {c [java.lang.String]]}\n"
              + ">",
          e.getMessage());
    }
  }

  @Test
  public void testStartsWith() {
    // GIVEN
    String expected = "test";
    String actual = "test value";

    // WHEN / THEN
    GAssert.assertStartsWith(expected, actual);
  }

  @Test
  public void testStartsWith_fail() {
    // GIVEN
    String expected = "value";
    String actual = "test value";

    try {
      // WHEN
      GAssert.assertStartsWith(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "Expected: a string starting with \"value\"\n"
              + "     but: was \"test value\" expected:<[]value> but was:<[test ]value>",
          e.getMessage());
    }
  }

  @Test
  public void testStartsWith_actualToShort_fail() {
    // GIVEN
    String expected = "value";
    String actual = "tes";

    try {
      // WHEN
      GAssert.assertStartsWith(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "Expected: a string starting with \"value\"\n"
              + "     but: was \"tes\" expected:<[value]> but was:<[tes]>",
          e.getMessage());
    }
  }

  @Test
  public void testStartsWith_fail_toShort() {
    // GIVEN
    String expected = "";
    String actual = "test value";

    try {
      // WHEN
      GAssert.assertStartsWith(expected, actual);
      fail();
    } catch (AssertionError e) {
      // THEN
      assertEquals(
          "expected value: \"\" is too short expected:<[]> but was:<[test value]>", e.getMessage());
    }
  }

  @Test
  public void testEndsWith() {
    // GIVEN
    String expected = "value";
    String actual = "test value";

    // WHEN / THEN
    GAssert.assertEndsWith(expected, actual);
  }

  @Test
  public void testEndsWith_fail() {
    // GIVEN
    String expected = "test";
    String actual = "test value";

    try {
      // WHEN
      GAssert.assertEndsWith(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "Expected: a string ending with \"test\"\n"
              + "     but: was \"test value\" expected:<test[]> but was:<test[ value]>",
          e.getMessage());
    }
  }

  @Test
  public void testEndsWith_actualToShort_fail() {
    // GIVEN
    String expected = "test";
    String actual = "lue";

    try {
      // WHEN
      GAssert.assertEndsWith(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "Expected: a string ending with \"test\"\n"
              + "     but: was \"lue\" expected:<[test]> but was:<[lue]>",
          e.getMessage());
    }
  }

  @Test
  public void testEndsWith_fail_toShort() {
    // GIVEN
    String expected = "";
    String actual = "test value";

    try {
      // WHEN
      GAssert.assertEndsWith(expected, actual);
      fail();
    } catch (AssertionError e) {
      // THEN
      assertEquals(
          "expected value: \"\" is too short expected:<[]> but was:<[test value]>", e.getMessage());
    }
  }

  @Test
  public void testNotEndsWith() {
    // GIVEN
    String expected = "value";
    String actual = "value test";

    // WHEN / THEN
    GAssert.assertNotEndsWith(expected, actual);
  }

  @Test
  public void testNotEndsWith_fail() {
    // GIVEN
    String expected = "test";
    String actual = "value test";

    try {
      // WHEN
      GAssert.assertNotEndsWith(expected, actual);
      fail();
    } catch (ComparisonFailure e) {
      // THEN
      assertEquals(
          "Expected: not a string ending with \"test\"\n"
              + "     but: was \"value test\" expected:<[]test> but was:<[value ]test>",
          e.getMessage());
    }
  }

  @Test
  public void testNotEndsWith_actualToShort_fail() {
    // GIVEN
    String expected = "test";
    String actual = "lue";

    // WHEN
    GAssert.assertNotEndsWith(expected, actual);

    // THEN

  }

  @Test
  public void testNotEndsWith_fail_toShort() {
    // GIVEN
    String expected = "";
    String actual = "test value";

    try {
      // WHEN
      GAssert.assertNotEndsWith(expected, actual);
      fail();
    } catch (AssertionError e) {
      // THEN
      assertEquals(
          "Expected: not a string ending with \"\"\n"
              + "     but: was \"test value\" expected:<[]> but was:<[test value]>",
          e.getMessage());
    }
  }
}
