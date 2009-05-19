package net.sourceforge.jwbf.mediawiki;


import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.UNKNOWN;
import static org.junit.Assert.assertArrayEquals;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.HttpAction;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Thomas Stock
 *
 */
public class VersionDetectionTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	@Test
	public void testMW109() throws Exception {
		Version [] vt = MWAction.findSupportedVersions(VersionSupportTest109.class);
		Version [] vx = {MW1_09};
		assertArrayEquals(vx, vt);
	
	}
	
	@Test
	public void testMW109110() throws Exception {
		Version [] vt = MWAction.findSupportedVersions(VersionSupportTest109110.class);
		Version [] vx = {MW1_09, MW1_10};
		assertArrayEquals(vx, vt);
	
	}
	
	@Test
	public void testMW109110ext() throws Exception {
		Version [] vt = MWAction.findSupportedVersions(VersionSupportTest109110ext.class);
		Version [] vx = {MW1_09, MW1_10};
		assertArrayEquals(vx, vt);
	
	}
	
	@Test
	public void testUnknown() throws Exception {
		Version [] vt = MWAction.findSupportedVersions(VersionSupportNotDef.class);
		Version [] vx = {UNKNOWN};
		assertArrayEquals(vx, vt);
	
	}
	
	@Test
	public void testInterface111112() throws Exception {
		Version [] vt = MWAction.findSupportedVersions(VersionSupportInDectable.class);
		Version [] vx = {MW1_11, MW1_12};
		assertArrayEquals(vx, vt);
	
	}
	
	@SupportedBy({ MW1_09 })
	protected class VersionSupportTest109 extends MWAction {

		@SuppressWarnings("deprecation")
		protected VersionSupportTest109() throws VersionException {
			super();
		}

		public HttpAction getNextMessage() {
			return null;
		}
		
	}
	@SupportedBy({ MW1_09, MW1_10 })
	protected class VersionSupportTest109110 extends MWAction {

		@SuppressWarnings("deprecation")
		protected VersionSupportTest109110() throws VersionException {
			super();
		}

		public HttpAction getNextMessage() {
			return null;
		}
		
	}
	
	protected class VersionSupportTest109110ext extends VersionSupportTest109110 {

		protected VersionSupportTest109110ext() throws VersionException {
			super();
		}

		
	}
	
	@SupportedBy({ MW1_11, MW1_12 })
	protected class VersionSupportInDectable  {
		
	}
	
	protected class VersionSupportNotDef extends MWAction {

		@SuppressWarnings("deprecation")
		protected VersionSupportNotDef() throws VersionException {
			super();
		}

		public HttpAction getNextMessage() {
			return null;
		}
		
	}

}
