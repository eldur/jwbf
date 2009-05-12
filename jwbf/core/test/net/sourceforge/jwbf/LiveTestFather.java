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
import java.net.URL;
import java.util.Collection;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

/**
 * @author loki
 *
 */
public class LiveTestFather {

	private static Properties data;
	
	private Random wheel = new Random();
	
	private static String filename;
	
	private final Collection<String> specialChars = new Vector<String>();
	
	protected LiveTestFather () {
		specialChars.add("\"");
		specialChars.add("\'");
		specialChars.add("?");
		specialChars.add("%");
		specialChars.add("&");
		specialChars.add("[");
		specialChars.add("]");
	}
	
	
	static {
		
		if (data == null) {
			data = new Properties();
			
			
			// find jwftestfile
			Collection<String> filepos = new Vector<String>();
			filepos.add(System.getProperty("user.home") + "/.jwbf/test.xml");
			filepos.add(System.getProperty("user.home") + "/jwbftest.xml");
			filepos.add("test.xml");
			for (String fname : filepos) {
				if (new File(fname).canRead()) {
					filename = fname;
					System.out.println("use testfile: " + filename);
					break;
				}
			}
			if (filename.length() < 1) {
				System.err.println("no testfile found. Use: " + System.getProperty("user.home") + "/.jwbf/test.xml");
				filename = System.getProperty("user.home") + "/.jwbf/test.xml";
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			try {
				data.loadFromXML(new FileInputStream(filename));
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				File f = new File(filename);
				try {
					f.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void main(String[] args) {
		System.out.println(new LiveTestFather().getRandomAlph(6));
		System.out.println(System.getenv());
		System.out.println(System.getProperties());
		System.out.println(System.getProperty("user.name"));
		System.out.println(System.getProperty("user.home"));
	}
	
	private static void addEmptyKey(String key) {
		data.put(key, " ");
		try {
			data.storeToXML(new FileOutputStream(filename), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	
	protected static URL getURL(final String key) throws Exception {
		URL u = new URL(getValue(key));
		String host = u.getHost();
		int port = u.getPort();
		if (port < 1) {
			port = 80;
		}
		String protocol = u.getProtocol();
		String file = u.getFile();
		return new URL(protocol, host, port, file);
	}
	
	protected static int getIntValue(final String key)  throws Exception {
		return Integer.parseInt(getValue(key));
	}
	protected Collection<String> getSpecialChars() {
		return specialChars;
	}
	protected String getRandomAlph(int length) {
		return getRandom(length, 65, 90);
	}
	protected String getRandom(int length) {
		return getRandom(length, 48, 126);
	}
	protected String getRandom(int length, int begin, int end) {
		String out = "";
		int charNum = 0;
		int count = 1;  
		while (count <= length) {  
            charNum = (wheel.nextInt(79) + begin);
            if (charNum >= begin && charNum <= end) {
            	
            	char d = (char) charNum;
                out += d;
                count++;
            }
        }
		return out;
	}
}
