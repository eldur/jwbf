package net.sourceforge.jwbf.zim.live;

import java.io.File;

import net.sourceforge.jwbf.TestHelper;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class BasicTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		TestHelper.prepareLogging();
		File dir = new File("zimTest");
		dir.mkdir();
		if (!(dir.isDirectory() && dir.exists())) {
			throw new Exception("no testdir");
		}
//		bots.add(new ZimWikiBot(dir));
		dir.delete();
	}
	@Before
	public void doNothing() {
		
	}
	@Test
	public void doLog() {
		Logger log = Logger.getLogger(getClass());
		log.info("Hello");
	}
	
}
