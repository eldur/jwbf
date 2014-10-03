package net.sourceforge.jwbf.core.actions.util;

import org.junit.Test;

public class PermissionExceptionTest {

  @Test(expected = PermissionException.class)
  public void testThrow() {
    throw new PermissionException("msg");
  }
}
