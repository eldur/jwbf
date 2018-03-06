package net.sourceforge.jwbf.core.bots.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class JwbfExceptionTest {

  @Test
  public void testGetExceptionSrcClass() {
    // GIVEN
    JwbfException testee = new JwbfException("Any");

    // WHEN
    Class<?> srcClass = testee.getExceptionSrcClass();

    // THEN
    assertEquals(JwbfExceptionTest.class, srcClass);
  }

  @Test
  public void testPrintStackTraceWithPrintStream() {
    // GIVEN
    String needleToFind = "TestWithPrintStream";
    JwbfException testee = new JwbfException(needleToFind);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);

    // WHEN
    testee.printStackTrace(ps);

    // THEN
    assertTrue(getString(baos).contains(needleToFind));
  }

  @Test
  public void testPrintStackTraceWithPrintWriter() {
    // GIVEN
    String needleToFind = "TestWithPrintWriter";
    JwbfException testee = new JwbfException(needleToFind);
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    // WHEN
    testee.printStackTrace(pw);

    // THEN
    assertTrue(sw.toString().contains(needleToFind));
  }

  @Test
  public void testGetClassByFail() {
    // GIVEN
    String element = "InvalidClassName";

    // WHEN
    Class<?> object = JwbfException.getClassBy(element);

    // THEN
    assertEquals(Object.class, object);
  }

  @Test
  public void testGetClassBy() {
    // GIVEN
    String element = "java.lang.String";

    // WHEN
    Class<?> object = JwbfException.getClassBy(element);

    // THEN
    assertEquals(String.class, object);
  }

  private String getString(ByteArrayOutputStream baos) {
    try {
      return baos.toString("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    }
  }
}
