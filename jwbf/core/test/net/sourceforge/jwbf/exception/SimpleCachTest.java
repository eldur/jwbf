package net.sourceforge.jwbf.exception;

import net.sourceforge.jwbf.LiveTestFather;
import net.sourceforge.jwbf.bots.util.JwbfException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class SimpleCachTest extends LiveTestFather {

	
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
