package net.sourceforge.jwbf.mediawiki.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.actions.util.ActionException;

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
    try {
      NullPointerException npe = new NullPointerException("oh it's null");

      throw new ActionException(npe);
    } catch (Exception e) {
      assertEquals(ActionException.class, e.getClass());
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw, true);
      e.printStackTrace(pw);
      String result = sw.getBuffer().toString();
      assertTrue(result.contains(JWBF.getPartId(getClass())));
      assertTrue(result.contains(JWBF.getVersion(getClass())));
    }

  }


}
