package net.sourceforge.jwbf.core.actions.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProcessExceptionTest {

  @Test
  public void testJoinMsgs() {
    // GIVEN
    ProcessException e = new ProcessException("original message");

    // WHEN
    ProcessException result = ProcessException.joinMsgs(e, "new message; ");

    // THEN
    assertEquals("new message; original message", result.getMessage());
    assertEquals(result.getMessage(), result.getLocalizedMessage());
  }
}
