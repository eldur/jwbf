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
package net.sourceforge.jwbf.actions.mw.util;

import net.sourceforge.jwbf.bots.util.JwbfException;

/**
 * 
 * @author Thomas Stock
 *
 */
public class ActionException extends JwbfException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 *
	 */
	public ActionException() {
		// do nothing
	}

	/**
	 * 
	 * @param arg0 exception text
	 */
	public ActionException(final String arg0) {
		super(arg0);
	}
	/**
	 * 
	 * @param arg0 sub
	 */
	public ActionException(Throwable arg0) {
		super(arg0);
	}
	/**
	 * 
	 * @param arg0 text
	 * @param arg1 sub
	 */
	public ActionException(final String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
