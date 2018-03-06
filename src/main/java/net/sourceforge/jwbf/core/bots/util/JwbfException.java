/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */
/** */
package net.sourceforge.jwbf.core.bots.util;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.google.common.annotations.VisibleForTesting;

import net.sourceforge.jwbf.JWBF;

/** @author Thomas Stock */
public class JwbfException extends RuntimeException {

  private static final long serialVersionUID = -2456904376052276104L;

  public JwbfException(String message) {
    super(message);
  }

  public JwbfException(Throwable t) {
    super(t);
  }

  public JwbfException(String message, Throwable t) {
    super(message, t);
  }

  public Class<?> getExceptionSrcClass() {
    return getClassBy(getStackTrace()[0]);
  }

  private static Class<?> getClassBy(StackTraceElement stackTraceElement) {
    String className = stackTraceElement.getClassName();
    return getClassBy(className);
  }

  @VisibleForTesting
  static Class<?> getClassBy(String className) {
    try {
      ClassLoader loader = JwbfException.class.getClassLoader();
      return loader.loadClass(className);
    } catch (ClassNotFoundException e) {
      return Object.class;
    }
  }

  private String getModulInfo() {
    Class<?> clazz = getExceptionSrcClass();
    return "( " + JWBF.getPartId(clazz) + "-" + JWBF.getVersion(clazz) + " )";
  }

  /** {@inheritDoc} */
  @Override
  public void printStackTrace(PrintWriter arg0) {
    arg0.println(getModulInfo());
    super.printStackTrace(arg0);
  }

  /** {@inheritDoc} */
  @Override
  public void printStackTrace(PrintStream s) {
    s.println(getModulInfo());
    super.printStackTrace(s);
  }
}
