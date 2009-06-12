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
	private Class < ? > clazz = Object.class;

	/**
	 * @param arg0 a
	 * @param clazz where exception was thrown
	 */
	public JwbfException(String arg0, Class < ? > clazz) {
		super(arg0);
		this.clazz = clazz;
	
	}
	
	/**
	 * Design for extension, use with {@link #setExceptionPackageClass(Class)}.
	 * @param arg0 a
	 * 
	 */
	protected JwbfException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0 a
	 * @param clazz where exception was thrown
	 */
	public JwbfException(Throwable arg0, Class < ? > clazz) {
		super(arg0);
		this.clazz = clazz;
	}

	/**
	 * Design for extension, use with {@link #setExceptionPackageClass(Class)}.
	 * @param arg0 a
	 * 
	 */
	protected JwbfException(Throwable arg0) {
		super(arg0);
	}
	
	/**
	 * 
	 * @return the
	 */
	public Class < ? > getExceptionSrcClass() {
		return clazz;
	}
	/**
	 * 
	 * @param clazz the
	 */
	protected void setExceptionPackageClass(Class < ? > clazz) {
		this.clazz = clazz;
	}
	/**
	 * 
	 * @return the
	 */
	private String getModulInfo() {
		return "( " + JWBF.getArtifactId(clazz) + "-" + JWBF.getVersion(clazz) + " )";
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
	public void printStackTrace() {
		System.err.println(getModulInfo());
		super.printStackTrace();
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
