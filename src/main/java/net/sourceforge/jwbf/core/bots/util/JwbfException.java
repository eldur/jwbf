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
/**
 * 
 */
package net.sourceforge.jwbf.core.bots.util;

import java.io.PrintStream;
import java.io.PrintWriter;

import net.sourceforge.jwbf.JWBF;

/**
 * @author Thomas Stock
 * 
 */
public class JwbfException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -2456904376052276104L;

  /**
   * @param message
   *          a
   */
  public JwbfException(String message) {
    super(message);
  }

  /**
   * @param t
   *          a
   */
  public JwbfException(Throwable t) {
    super(t);
  }

  /**
   * @param message
   *          a
   * @param t
   *          a
   */
  public JwbfException(String message, Throwable t) {
    super(message, t);
  }

  /**
   * @return the
   */
  public Class<?> getExceptionSrcClass() {
    return getStackTraceClass();
  }

  private Class<?> getStackTraceClass() {

    ClassLoader loader = getClass().getClassLoader();
    try {
      return loader.loadClass(getStackTrace()[0].getClassName());
    } catch (ClassNotFoundException e) {
      return Object.class;
    }

  }

  /**
   * 
   * @return the
   */
  private String getModulInfo() {

    Class<?> clazz = getStackTraceClass();

    return "( " + JWBF.getPartId(clazz) + "-" + JWBF.getVersion(clazz) + " )";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void printStackTrace(PrintWriter arg0) {
    arg0.println(getModulInfo());
    super.printStackTrace(arg0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void printStackTrace(PrintStream s) {
    s.println(getModulInfo());
    super.printStackTrace(s);
  }

}
