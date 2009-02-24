package net.sourceforge.jwbf.live;

import static org.junit.Assert.assertTrue;
import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.HttpBot;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HttpBotTest extends LiveTestFather {


	private Logger log = Logger.getLogger(getClass());
	private HttpBot bot;
	
	/**
	 * Setup log4j.
	 * @throws Exception a
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
		
	}
	@Before
	public void prepare() {
		bot = new HttpBot() {
		};
	}
	/**
	 * Test write and read 
	 * @throws Exception a
	 */
	@Test
	public final void findContent() throws Exception {

		String s = bot.getPage("http://localhost/");
		assertTrue("content shuld be longer then one", s.length() > 1);
	}
	

}
