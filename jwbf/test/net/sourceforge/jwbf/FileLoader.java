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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Hepler class to read in test files.
 * @author Thomas Stock
 *
 */
public final class FileLoader {
	
	public static final String MW1_10 = "testHtml/mw1_10/";
	public static final String MW1_9_3 = "testHtml/mw1_9_3/";
	
	/**
	 * 
	 *
	 */
	private FileLoader() {
//		do nothing
	}
	/**
	 * 
	 * @param f file
	 * @return content of file
	 */
	public static String readFromFile(final File f) {
		  String thisLine;
		  String temp = "";
		   try {
		       BufferedReader in = new BufferedReader(
		                     new FileReader(f));
		       try {
		           while ((thisLine = in.readLine()) != null) {
		        	   temp += thisLine;
		           }
		           in.close();
		       } catch (IOException e) {
		           System.out.println("Read error " + e);
		       }
		   } catch (IOException e) {
		       System.out.println("Open error " + e);
		   }
		   return temp;
	}
	/**
	 * 
	 * @param s file
	 * @return content of file
	 */
	public static String readFromFile(final String s) {
		   File f = new File(s);
		   return readFromFile(f);
	}

}
