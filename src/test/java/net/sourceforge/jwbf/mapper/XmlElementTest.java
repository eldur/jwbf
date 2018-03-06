package net.sourceforge.jwbf.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class XmlElementTest {

  @Test
  public void testInitNull() {
    XmlElement xmlElement = new XmlElement(null);
    assertNull(xmlElement.getQualifiedName());
    assertNull(xmlElement.getAttributeValue(""));
    assertNull(xmlElement.getChildAttributeValue("", ""));
    assertNull(xmlElement.getText());

    assertEquals(XmlElement.NULL_XML, xmlElement.getChild(""));

    assertTrue(xmlElement.getChildren().isEmpty());
    assertTrue(xmlElement.getChildren("").isEmpty());

    assertFalse(xmlElement.hasAttribute(""));
  }
}
