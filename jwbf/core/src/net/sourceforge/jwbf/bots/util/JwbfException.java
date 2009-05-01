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
package net.sourceforge.jwbf.bots.util;

import java.io.PrintStream;
import java.io.PrintWriter;

import net.sourceforge.jwbf.JWBF;

/**
 * @author Thomas Stock
 *
 */
public class JwbfException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2456904376052276104L;


	/**
	 * @param arg0 a
	 */
	public JwbfException(String arg0) {
		super(arg0);
	
	}

	/**
	 * @param arg0 a
	 */
	public JwbfException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0 a
	 * @param arg1 a
	 */
	public JwbfException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	
	@Override
	public void printStackTrace(PrintWriter arg0) {
		arg0.println("JWBF.version:" + JWBF.getVersion());
		super.printStackTrace(arg0);
	}

	@Override
	public void printStackTrace() {
		System.out.println("JWBF.version:" + JWBF.getVersion());
		super.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		s.println("JWBF.version:" + JWBF.getVersion());
		super.printStackTrace(s);
	}

	
}
