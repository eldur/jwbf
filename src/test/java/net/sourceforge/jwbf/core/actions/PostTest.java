package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Locale;

import org.junit.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;

import net.sourceforge.jwbf.GAssert;

public class PostTest {

  @Test
  public void testEquals() {

    // GIVEN
    Post a = new Post("http://localhost/");
    Post b = new Post("http://localhost/wiki/");
    Post c = new Post("http://localhost/wiki/", "utf-8");
    Post d = new Post("http://localhost/wiki/", "utf-16");
    Post e = RequestBuilder.of("http://localhost/").postParam("a", "b").buildPost();

    // WHEN/THEN
    assertTrue(a.equals(a));
    assertTrue(c.equals(b));
    assertFalse(c.equals(d));
    assertFalse(a.equals(b));
    assertFalse(a.equals(null));
    assertFalse(a.equals(c));
    assertFalse(a.equals(e));
    assertFalse(a.equals(this));
  }

  @Test
  public void testHashCode() {
    // GIVEN
    Post a = new Post("http://localhost/");
    Post b = new Post("http://localhost/wiki/");
    Post c = new Post("http://localhost/wiki/", "utf-8");

    // WHEN/THEN
    assertEquals(b.hashCode(), c.hashCode());
    assertNotEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void test() {
    // GIVEN
    String url = "http://localhost/";
    Post post = new Post(url);

    // WHEN
    String request = post.getRequest();
    ImmutableMultimap<String, Object> params = post.getParams();

    // THEN
    assertEquals(url, request);
    assertTrue(params.isEmpty());
  }

  @Test
  public void testWithParam() {
    // GIVEN
    String url = "http://localhost/";
    Post post = new Post(url).postParam("a", "b");

    // WHEN
    String request = post.getRequest();
    ImmutableMultimap<String, Object> params = post.getParams();

    // THEN
    assertEquals(url, request);
    assertEquals(ImmutableMultimap.of("a", "b"), params);
  }

  @Test
  public void testWithParamInt() {
    // GIVEN
    String url = "http://localhost/";

    // WHEN
    ImmutableMultimap<String, Object> postWithInt =
        RequestBuilder.of(url) //
            .postParam("a", 5) //
            .buildPost()
            .getParams();

    // THEN
    GAssert.assertEquals(ImmutableMultimap.of("a", "5"), postWithInt);
  }

  @Test
  public void testWithParamDouble0() {
    // GIVEN
    String url = "http://localhost/";

    // WHEN
    ImmutableMultimap<String, Object> postWithDouble =
        RequestBuilder.of(url) //
            .postParam("a", 5d, Locale.US, "%1.0f") //
            .buildPost()
            .getParams();

    // THEN
    GAssert.assertEquals(ImmutableMultimap.of("a", "5"), postWithDouble);
  }

  @Test
  public void testWithParamDouble() {
    // GIVEN
    String url = "http://localhost/";

    // WHEN
    ImmutableMultimap<String, Object> postWithDouble =
        RequestBuilder.of(url) //
            .postParam("a", 5.000_00d, Locale.US, "%4.3f") //
            .buildPost()
            .getParams();

    // THEN
    GAssert.assertEquals(ImmutableMultimap.of("a", "5.000"), postWithDouble);
  }

  @Test
  public void testWithParamDouble1() {
    // GIVEN
    String url = "http://localhost/";

    // WHEN
    ImmutableMultimap<String, Object> postWithDouble =
        RequestBuilder.of(url) //
            .postParam("a", 5.000_1d, Locale.GERMANY, "%.4f") //
            .buildPost()
            .getParams();

    // THEN
    GAssert.assertEquals(ImmutableMultimap.of("a", "5,0001"), postWithDouble);
  }

  @Test
  public void testWithParamFile() {
    // GIVEN
    String url = "http://localhost/";
    File file = new File("test");

    // WHEN
    ImmutableMultimap<String, Object> postWithInt =
        RequestBuilder.of(url) //
            .postParam("a", file) //
            .buildPost()
            .getParams();

    // THEN
    GAssert.assertEquals(ImmutableMultimap.of("a", file), postWithInt);
  }

  @Test
  public void testWithSameParams() {
    // GIVEN
    String url = "http://localhost/";
    Post post =
        new Post(url) //
            .postParam("a", "b") //
            .postParam("a", "b") //
            .postParam("a", "c") //
        ;

    // WHEN
    String request = post.getRequest();
    ImmutableMultimap<String, Object> params = post.getParams();

    // THEN
    assertEquals(url, request);
    assertEquals(ImmutableMultimap.of("a", "b", "a", "b", "a", "c"), params);
  }

  @Test
  public void testToBuilderFailed() {
    try {
      new Post("a").toBuilder();
      fail();
    } catch (UnsupportedOperationException e) {
      assertEquals("only supported when type was created with a builder", e.getMessage());
    }
  }

  @Test
  public void testDeprecatedPostParams() {
    Post post = new Post("uuuh");
    post.addParam("a", "b");
    assertEquals("uuuh UTF-8 {a=[b]}", post.toString());
  }

  @Test
  public void testHiddenConstructors() {
    Post postA = new Post(Suppliers.ofInstance("a"), "UTF-8");
    Post postB = new Post("a", "UTF-8");
    assertEquals(postA, postB);
  }
}
