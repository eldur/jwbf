package net.sourceforge.jwbf.mediawiki.contentRep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class CategoryItemTest {

  @Test
  public void testEquals() {
    CategoryItem a = new CategoryItem("A", 0, 0);
    CategoryItem a2 = new CategoryItem("A", 0, 0);
    CategoryItem b = new CategoryItem("B", 0, 0);
    CategoryItem b1 = new CategoryItem("B", 1, 0);
    CategoryItem b11 = new CategoryItem("B", 1, 1);
    assertEquals(a, a);
    assertEquals(a, a2);
    assertNotEquals(a, b);
    assertNotEquals(a, b1);
    assertNotEquals(a, b11);
    assertNotEquals(a, new Object());
  }

  @Test
  public void testHashCode() {
    CategoryItem a = new CategoryItem("A", 0, 0);
    CategoryItem a2 = new CategoryItem("A", 0, 0);

    CategoryItem b = new CategoryItem("B", 0, 0);

    assertEquals(a.hashCode(), a.hashCode());
    assertEquals(a.hashCode(), a2.hashCode());

    assertNotEquals(a.hashCode(), b.hashCode());
  }
}
