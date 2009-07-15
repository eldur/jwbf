package net.sourceforge.jwbf.live.zim;

import java.io.File;

import net.sourceforge.jwbf.bots.ZimWikiBot;
import net.sourceforge.jwbf.live.mediawiki.ArticleTest;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
public class BasicTest extends ArticleTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		File dir = new File("zimTest");
		dir.mkdir();
		if (!(dir.isDirectory() && dir.exists())) {
			throw new Exception("no testdir");
		}
		bots.add(new ZimWikiBot(dir));
		dir.delete();
	}
	@Before
	public void doNothing() {
		
	}
	
	
}
