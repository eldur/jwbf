package net.sourceforge.jwbf.mediawiki.contentRep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class SimpleFileTest {

  @Test
  public void testHashCode() {

    SimpleFile testeeA = new SimpleFile("Test");
    SimpleFile testeeB = new SimpleFile("Test");
    assertEquals(testeeA.hashCode(), testeeB.hashCode());

  }

  @Test
  public void testEquals() {
    SimpleFile a = new SimpleFile("A");
    assertEquals(a, a);

    SimpleFile b = new SimpleFile("B");
    assertNotEquals(a, b);

    SimpleFile c = new SimpleFile("B");
    assertEquals(b, c);

    SimpleFile d = new SimpleFile("B");
    d.setText("Text");
    assertNotEquals(b, d);
  }
}
