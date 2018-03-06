package net.sourceforge.jwbf.core.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class ParamTupleTest {

  @Test
  public void testInitNullKey() {
    try {
      new ParamTuple(null, "value");
      fail();
    } catch (NullPointerException e) {
      assertEquals("key must not be null", e.getMessage());
    }
  }

  @Test
  public void testInitNullValue() {
    try {
      new ParamTuple("key", (Supplier) null);
      fail();
    } catch (NullPointerException e) {
      assertEquals("value must not be null", e.getMessage());
    }
  }

  @Test
  public void testInitNullStringValue() {
    try {
      new ParamTuple("key", (String) null);
      fail();
    } catch (NullPointerException e) {
      assertEquals("value must not be null", e.getMessage());
    }
  }

  @Test
  public void testHashCode() {
    ParamTuple a = new ParamTuple("a", "b");
    ParamTuple b = new ParamTuple("a", Suppliers.ofInstance("b"));
    ParamTuple c = new ParamTuple("b", "c");

    assertEquals(a.hashCode(), a.hashCode());
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(a.hashCode(), c.hashCode());
  }

  @Test
  public void testEquals() {
    ParamTuple a = new ParamTuple("a", "b");
    ParamTuple b = new ParamTuple("a", Suppliers.ofInstance("b"));
    ParamTuple c = new ParamTuple("b", "c");
    ParamTuple d = new ParamTuple("a", "d");

    assertTrue(a.equals(a));
    assertTrue(a.equals(b));
    assertFalse(a.equals(c));
    assertFalse(a.equals(d));
    assertFalse(a.equals(new Object()));
    assertFalse(a.equals(null));
  }

  @Test
  public void testToString() {
    ParamTuple a = new ParamTuple("a", "b");

    assertEquals("('a', 'b')", a.toString());
  }
}
