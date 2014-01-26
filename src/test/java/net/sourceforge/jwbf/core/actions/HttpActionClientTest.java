package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpActionClientTest {

  private HttpActionClient testee;

  @Test
  public void testHostUrl() {
    // GIVEN
    testee = HttpActionClient.of("http://localhost/");

    // WHEN/THEN
    assertEquals("http://localhost", testee.getHostUrl());
    assertEquals("http://localhost/", testee.getUrl());
  }

  @Test
  public void testHostUrlWithPath() {
    // GIVEN
    testee = HttpActionClient.of("https://localhost/a/b.html?a=b");

    // WHEN/THEN
    assertEquals("https://localhost", testee.getHostUrl());
    assertEquals("https://localhost/a/b.html?a=b", testee.getUrl());
  }

}
