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
package net.sourceforge.jwbf;
/**
 * 
 * @author Thomas Stock
 *
 */
public final class JWBF {


	private static final String VERSION;
	
	static {
		VERSION = readVersion();
	}
	
	/**
	 * 
	 *
	 */
	private JWBF() {
//		do nothing 
	}
	/**
	 * 
	 * @return the version
	 */
	public static String getVersion() {
		return VERSION;
	}
	/**
	 * Prints the JWBF Version.
	 */
	public static void printVersion() {
		System.out.println("JWBF Version: " + VERSION);
	}

	/**
	 * 
	 * @return the version from manifest
	 */
	private static String readVersion() {

		String implementationVersion = JWBF.class.getPackage()
				.getImplementationVersion();

		if (implementationVersion == null) {
			return "DEVEL";
		} else {
			return prepareVersion(implementationVersion);
		}

	}
	/**
	 * 
	 * @param implementationVersion raw
	 * @return formated version
	 */
	private static String prepareVersion(String implementationVersion) {
		String out = "";
		char[] numbers = new char[implementationVersion.length()];
		implementationVersion.getChars(0, implementationVersion.length(), numbers, 0);
		for (int i = 0; i < numbers.length; i++) {
			if (i == numbers.length - 2) {
				out += numbers[i] + "";
			} else {
				out += numbers[i] + ".";
			}
			
		}
		
		
		return out.substring(0, out.length() - 1);
	}

}
