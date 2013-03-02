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
package net.sourceforge.jwbf.core.actions.util;

import net.sourceforge.jwbf.core.bots.util.JwbfException;

/**
 * 
 * @author Thomas Stock
 */
public class ActionException extends JwbfException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message
   * @param t
   */
  public ActionException(String message, Throwable t) {
    super(message, t);
  }

  /**
   * @param message
   */
  public ActionException(String message) {
    super(message);
  }

  /**
   * @param t
   */
  public ActionException(Throwable t) {
    super(t);
  }

}
