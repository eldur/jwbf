package net.sourceforge.jwbf.live;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetImagelinkTitlesTest extends LiveTestFather {


	private MediaWikiBot bot = null;
	
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
//		prepareTestWikis();
	}
	@Test
	public void testNothing() throws Exception {
//		TODO write it 
	}
}
