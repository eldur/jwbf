package net.sourceforge.jwbf.live;

import java.io.File;
import java.io.FileInputStream;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileDownloadTest extends LiveTestFather {

	

	private MediaWikiBot bot = null;
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	
	/**
	 * Test category read. Test category must have more then 50 members.
	 * @throws Exception a
	 */
	@Test
	public final void uploadMW1_09() throws Exception {
		
		bot = new MediaWikiBot(getValue("wikiMW1_09_url"));
		bot.login(getValue("wikiMW1_09_user"),
				getValue("wikiMW1_09_pass"));

		byte [] s = bot.getBytes("http://localhost/mediawiki-1.9.6/images/a/aa/Testfile.gif");
		
		File file = new File("testfile.gif");
		file.createNewFile();
		byte buff1[]=new byte[512];
//		System.out.println(file.length());
		FileInputStream fis = new FileInputStream(file);
		int read = fis.read(buff1);
		for(int i =0; i<read; i++) {
		 System.out.print(buff1[i] + "  ");
		 System.out.println(s[i]);
		}
		fis.close();
		file.deleteOnExit();
//		FileOutputStream fs = new FileOutputStream("test.gif");
//		
//		fs.write(s);
//		fs.close();
	}
}
