package net.sourceforge.jwbf.mediawiki;

import static com.google.common.collect.ImmutableList.of;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.DEVELOPMENT;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.UNKNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

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

    assertTrue(MW1_18.greaterEqThen(MW1_16));
    assertTrue(MW1_18.greaterEqThen(MW1_17));
    assertTrue(MW1_18.greaterEqThen(MW1_17));
    assertFalse(MW1_19.greaterEqThen(MW1_20));
  }

  // @Test
  // public void testGetLastVersion() {
  //
  // assertTrue(MW1_15 == Version.getLast());
  //
  // }

  @Test
  public void testMW_A() {
    List<Version> vt = MWAction.findSupportedVersions(VersionSupportTest_A.class);
    List<Version> vx = of(MW1_17);
    assertEquals(vx, vt);

  }

  @Test
  public void testMW_A_B() {
    List<Version> vt = MWAction.findSupportedVersions(VersionSupportTest_A_B.class);
    List<Version> vx = of(MW1_17, MW1_18);
    assertEquals(vx, vt);

  }

  @Test
  public void testMW_A_B_ext() {
    List<Version> vt = MWAction.findSupportedVersions(VersionSupportTest_A_B_ext.class);
    List<Version> vx = of(MW1_17, MW1_18);
    assertEquals(vx, vt);

  }

  @Test
  public void testUnknown() {
    List<Version> vt = MWAction.findSupportedVersions(VersionSupportNotDef.class);
    List<Version> vx = of(UNKNOWN);
    assertEquals(vx, vt);

  }

  @Test
  public void testInterface_C_D() {
    List<Version> vt = MWAction.findSupportedVersions(VersionSupportInDectable.class);
    List<Version> vx = of(MW1_19, MW1_20);
    assertEquals(vx, vt);

  }

  @SupportedBy({ MW1_17 })
  protected class VersionSupportTest_A extends MWAction {

    @SuppressWarnings("deprecation")
    protected VersionSupportTest_A() {
      super();
    }

    public HttpAction getNextMessage() {
      return null;
    }

  }

  @SupportedBy({ MW1_17, MW1_18 })
  protected class VersionSupportTest_A_B extends MWAction {

    @SuppressWarnings("deprecation")
    protected VersionSupportTest_A_B() {
      super();
    }

    public HttpAction getNextMessage() {
      return null;
    }

  }

  protected class VersionSupportTest_A_B_ext extends VersionSupportTest_A_B {

    protected VersionSupportTest_A_B_ext() {
      super();
    }

  }

  @SupportedBy({ MW1_19, MW1_20 })
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
