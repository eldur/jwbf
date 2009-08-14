package net.sourceforge.jwbf.core.exception;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.bots.util.JwbfException;
import net.sourceforge.jwbf.inyoka.actions.GetRevision;

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
		JWBF.printVersion();
		System.out.println(JWBF.getPartId(GetRevision.class));
		
		try {
			throw new JwbfException("sdf"); // FIXME doesn't work good
		} catch (Exception e) {
			e.printStackTrace();
		}
	

	}
	
	
}
