package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;

import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import org.junit.Test;

public class NsTest {

  @Test
  public final void testNsCreate() {

    String s =
        MWAction.createNsString(MediaWiki.NS_MAIN, MediaWiki.NS_TEMPLATE, MediaWiki.NS_CATEGORY);
    assertEquals(MediaWiki.NS_MAIN + "|" + MediaWiki.NS_TEMPLATE + "|" + MediaWiki.NS_CATEGORY, s);
  }

  @Test
  public final void testEntities() {

    String s = "&#039;";
    String t = "'";
    assertEquals(t, MediaWiki.htmlUnescape(s));
    s = "&quot;";
    t = "\"";
    assertEquals(t, MediaWiki.htmlUnescape(s));
  }

}
