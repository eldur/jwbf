package net.sourceforge.jwbf.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.google.common.io.Resources;
import net.sourceforge.jwbf.TestHelper;
import org.junit.Test;

public class XmlConverterTest {

  @Test
  public void testGetChildSeq_first() {
    // GIVEN
    String xml = TestHelper.textOf(Resources.getResource("mediawiki/any/embeddedin_1.xml"));

    // WHEN
    XmlElement first = XmlConverter.getChild(xml, "query");

    // THEN
    assertEquals("query", first.getQualifiedName());
  }

  @Test
  public void testGetChildSeq_first_second() {
    // GIVEN
    String xml = TestHelper.textOf(Resources.getResource("mediawiki/any/embeddedin_1.xml"));

    // WHEN
    XmlElement result = XmlConverter.getChild(xml, "query", "embeddedin");

    // THEN
    assertEquals("embeddedin", result.getQualifiedName());
  }

  @Test
  public void testGetChildSeq_null() {
    // GIVEN
    String xml = TestHelper.textOf(Resources.getResource("mediawiki/any/intoken_fail.xml"));

    // WHEN
    XmlElement first = XmlConverter.getChild(xml, null);

    // THEN
    assertNull(first);
  }

  @Test(expected = NullPointerException.class)
  public void testGetChildSeq_null_in_sequence() {
    // GIVEN
    String xml = TestHelper.textOf(Resources.getResource("mediawiki/any/intoken_fail.xml"));

    // WHEN
    XmlConverter.getChild(xml, "", null, "");
    fail();
  }

  @Test
  public void testGetChildSeq_unknown() {
    // GIVEN
    String xml = TestHelper.textOf(Resources.getResource("mediawiki/any/intoken_fail.xml"));

    // WHEN
    XmlElement first = XmlConverter.getChild(xml, "a", "b", "c");

    // THEN
    assertNull(first);
  }
}
