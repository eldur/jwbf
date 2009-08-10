package net.sourceforge.jwbf.exception;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.bots.util.JwbfException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class SimpleCachTest extends TestHelper {

	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	

	@Test
	public void basic1() throws Exception {

		try {
			throw new JwbfException("sdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
	
	
}
