package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class GetTest {

  @Test
  public void testEquals() {

    // GIVEN
    Get a = new Get("http://localhost/");
    Get b = new Get("http://localhost/wiki/");
    Get c = new Get("http://localhost/wiki/", "utf-8");
    Get d = new Get("http://localhost/wiki/", "utf-16");

    // WHEN/THEN
    assertTrue(a.equals(a));
    assertTrue(c.equals(b));
    assertFalse(c.equals(d));
    assertFalse(a.equals(b));
    assertFalse(a.equals(null));
    assertFalse(a.equals(c));
    assertFalse(a.equals(this));
  }

  @Test
  public void testHashCode() {
    // GIVEN
    Get a = new Get("http://localhost/");
    Get b = new Get("http://localhost/wiki/");
    Get c = new Get("http://localhost/wiki/", "utf-8");

    // WHEN/THEN
    assertEquals(b.hashCode(), c.hashCode());
    assertNotEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void testToString() {
    assertEquals("http://localhost/wiki/ UTF-8", new Get("http://localhost/wiki/").toString());
  }

  @Test
  public void testToBuilderFailed() {
    try {
      new Get("a").toBuilder();
      fail();
    } catch (UnsupportedOperationException e) {
      assertEquals("only supported when type was created with a builder", e.getMessage());
    }
  }
}
