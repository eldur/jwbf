package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PostTest {

  @Test
  public void test() {
    // GIVEN
    String url = "http://localhost/";
    Post post = new Post(url);

    // WHEN
    String request = post.getRequest();

    // THEN
    assertEquals(url, request);
  }

}
