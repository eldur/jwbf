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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

public class LiveTestFather {

	private static Properties data;
	private static final String filename = "test.xml";
	private Random wheel = new Random();
	
	static {
		if (data == null) {
			data = new Properties();
			try {
				data.loadFromXML(new FileInputStream(filename));
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				File f = new File(filename);
				try {
					f.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	private static void addEmptyKey(String key) {
		data.put(key, " ");
		try {
			data.storeToXML(new FileOutputStream(filename), "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected static String getValue(final String key) throws Exception {
		if (!data.containsKey(key) || data.getProperty(key).trim().length() <= 0) {
			addEmptyKey(key);
			throw new Exception("No or empty value for key: \"" + key + "\" in " + filename);
		}
		return data.getProperty(key);
	}
	
	protected static int getIntValue(final String key)  throws Exception {
		return Integer.parseInt(getValue(key));
	}

	protected String getRandom(int length) {
		String out = "";
		int charNum = 0;
		int count = 1;  
		while(count <= length)
        {  
            charNum = (wheel.nextInt(79) + 48);
            if (charNum >= 48 && charNum <= 126)
            {
            	
            	char d = (char)charNum;
                out += d;
                count++;
            }
        }
		return out;
	}
}
