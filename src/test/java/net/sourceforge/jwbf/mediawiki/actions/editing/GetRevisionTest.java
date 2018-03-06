package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GetRevisionTest {

  @Test
  public void testGetDataProperties() {
    assertEquals("comment", GetRevision.getDataProperties(GetRevision.COMMENT));

    assertEquals(
        "content%7Ccomment", //
        GetRevision.getDataProperties(GetRevision.CONTENT | GetRevision.COMMENT));

    assertEquals(
        "user%7Cids", //
        GetRevision.getDataProperties(GetRevision.IDS | GetRevision.USER | GetRevision.IDS));
  }
}
