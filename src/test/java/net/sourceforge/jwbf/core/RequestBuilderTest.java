package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RequestBuilderTest {

  @Test
  public void testBuild() {
    assertEquals("a", new RequestBuilder("a") //
        .build());
    assertEquals("/a?b=c", new RequestBuilder("/a") //
        .param("b", "c") //
        .build());
    assertEquals("/a?b=c", new RequestBuilder("/a") //
        .param("b", "c") //
        .param("b", "c") //
        .build());
    assertEquals("/a?d=e&b=c", new RequestBuilder("/a") //
        .param("d", "e") //
        .param("b", "c") //
        .param("d", "e") //
        .build());
  }

}
