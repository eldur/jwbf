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
 * This exception can be used in children of {@link MWAction} to handle 
 * exceptions in these actions, like access to content or mismatching patterns e.g.
 * 
 * @author Thomas Stock
 *
 */
public class ProcessException extends JwbfException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3830701798846228121L;

	
	/**
	 * 
	 */
	public ProcessException() {
		super();
	}
	
	/**
	 * 
	 * @param msg exception text
	 */
	public ProcessException(final String msg) {
		super(msg);
	}
}
