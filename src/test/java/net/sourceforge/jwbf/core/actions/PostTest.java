package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableMultimap;
import org.junit.Test;

public class PostTest {

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
  public void testWithSameParams() {
    // GIVEN
    String url = "http://localhost/";
    Post post = new Post(url) //
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
}
