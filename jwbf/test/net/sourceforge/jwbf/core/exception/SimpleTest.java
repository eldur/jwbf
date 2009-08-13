package net.sourceforge.jwbf.core.exception;

import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.JWBF;
import net.sourceforge.jwbf.core.bots.util.JwbfException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * 
 * @author Thomas Stock
 *
 */
public class SimpleTest extends TestHelper {

	
	@BeforeClass
	public static void setUp() throws Exception {
		PropertyConfigurator.configureAndWatch("test4log4j.properties",
				60 * 1000);
	}
	

	@Test
	public void basic1() throws Exception {

		System.out.println(JWBF.getArtifactId(SimpleTest.class));
		
		try {
			throw new JwbfException("sdf"); // FIXME doesn't work good
		} catch (Exception e) {
			e.printStackTrace();
		}
	

	}
	
	
}
