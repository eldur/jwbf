package net.sourceforge.jwbf.mediawiki.contentRep;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LogItemTest {

  @Test
  public void testToString() {

    // GIVEN
    LogItem testee = new LogItem("a", "b", "c");

    // WHEN / THEN
    assertEquals("a", testee.getTitle());
    assertEquals("b", testee.getType());
    assertEquals("c", testee.getUser());

    assertEquals("* a was b by c", testee.toString());
  }
}
