package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

public class GetPageTest {

  @Test
  public void testGetMessage() {
    // GIVEN
    String url = "http://localhost/wiki/index.php";

    // WHEN
    GetPage testee = new GetPage(url);

    // THEN
    HttpAction expected = RequestBuilder.of("http://localhost/wiki/index.php").buildGet();
    assertEquals(expected, testee.getNextMessage());
  }

  @Test
  public void testGetMessageWithParams() {
    // GIVEN
    String url = "http://localhost/wiki/index.php?key=value";

    // WHEN
    GetPage testee = new GetPage(url);

    // THEN
    HttpAction expected = RequestBuilder.of("http://localhost/wiki/index.php?key=value").buildGet();
    assertEquals(expected, testee.getNextMessage());
  }
}
