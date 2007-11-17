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
package net.sourceforge.jwbf.live;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class LiveTest {

	private static Properties data;
	private static final String filename = "test.xml";
	
	protected LiveTest() {
		if (data == null) {
			data = new Properties();
			try {
				data.loadFromXML(new FileInputStream(filename));
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	protected String getValue(final String key) throws Exception {
		if (!data.containsKey(key) || data.getProperty(key).length() <= 0) {
			throw new Exception("No or empty value for key: \"" + key + "\" in " + filename);
		}
		return data.getProperty(key);
	}
	protected int getIntValue(final String key)  throws Exception {
		return Integer.parseInt(getValue(key));
	}

}
