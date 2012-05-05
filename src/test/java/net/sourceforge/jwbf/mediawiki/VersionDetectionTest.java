package net.sourceforge.jwbf.mediawiki;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.DEVELOPMENT;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.UNKNOWN;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;

import org.junit.Test;

/**
 * @author Thomas Stock
 * 
 */
public class VersionDetectionTest {

  @Test
  public void testGetAllVersion() {

    Version[] va = Version.valuesStable();
    for (int i = 0; i < va.length; i++) {
      if (va[i].equals(UNKNOWN) || va[i].equals(DEVELOPMENT)) {
        fail("bad version");
      }
    }
    assertTrue("shuld be greater then", va.length - 2 > 1);
  }

  @Test
  public void testVersionEq() {

    assertTrue(MW1_11.greaterEqThen(MW1_09));
    assertTrue(MW1_11.greaterEqThen(MW1_10));
    assertTrue(MW1_11.greaterEqThen(MW1_11));
    assertFalse(MW1_11.greaterEqThen(MW1_12));
  }

  // @Test
  // public void testGetLastVersion() {
  //
  // assertTrue(MW1_15 == Version.getLast());
  //
  // }

  @Test
  public void testMW109() {
    Version[] vt = MWAction.findSupportedVersions(VersionSupportTest109.class);
    Version[] vx = { MW1_09 };
    assertArrayEquals(vx, vt);

  }

  @Test
  public void testMW109110() {
    Version[] vt = MWAction
        .findSupportedVersions(VersionSupportTest109110.class);
    Version[] vx = { MW1_09, MW1_10 };
    assertArrayEquals(vx, vt);

  }

  @Test
  public void testMW109110ext() {
    Version[] vt = MWAction
        .findSupportedVersions(VersionSupportTest109110ext.class);
    Version[] vx = { MW1_09, MW1_10 };
    assertArrayEquals(vx, vt);

  }

  @Test
  public void testUnknown() {
    Version[] vt = MWAction.findSupportedVersions(VersionSupportNotDef.class);
    Version[] vx = { UNKNOWN };
    assertArrayEquals(vx, vt);

  }

  @Test
  public void testInterface111112() {
    Version[] vt = MWAction
        .findSupportedVersions(VersionSupportInDectable.class);
    Version[] vx = { MW1_11, MW1_12 };
    assertArrayEquals(vx, vt);

  }

  @SupportedBy({ MW1_09 })
  protected class VersionSupportTest109 extends MWAction {

    @SuppressWarnings("deprecation")
    protected VersionSupportTest109() {
      super();
    }

    public HttpAction getNextMessage() {
      return null;
    }

  }

  @SupportedBy({ MW1_09, MW1_10 })
  protected class VersionSupportTest109110 extends MWAction {

    @SuppressWarnings("deprecation")
    protected VersionSupportTest109110() {
      super();
    }

    public HttpAction getNextMessage() {
      return null;
    }

  }

  protected class VersionSupportTest109110ext extends VersionSupportTest109110 {

    protected VersionSupportTest109110ext() {
      super();
    }

  }

  @SupportedBy({ MW1_11, MW1_12 })
  protected class VersionSupportInDectable {

  }

  protected class VersionSupportNotDef extends MWAction {

    @SuppressWarnings("deprecation")
    protected VersionSupportNotDef() {
      super();
    }

    public HttpAction getNextMessage() {
      return null;
    }

  }

}
