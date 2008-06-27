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


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;

import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.LoginData;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 * This Message can be used to find variables, you need to process modification
 * and deletion. 
 * @author Thomas Stock
 * 
 *
 *
 */
public class GetEnvironmentVars extends MWAction {

	private Hashtable<String, String> tab;
	private static final Logger LOG = Logger.getLogger(GetEnvironmentVars.class);
	
	/**
	 * 
	 * @param name of article
	 * @param tab ref on a tabel with inner values
	 * @param login a
	 */
	public GetEnvironmentVars(final String name, Hashtable<String, String> tab, LoginData login) {
		String uS = "";
		this.tab = tab;
		try {
			uS = "/index.php?title="
			+ URLEncoder.encode(name, MediaWikiBot.CHARSET) + "&action=edit&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));
	}

	/**
	 * @return the returning text
	 * @param s the whole returning text
	 * @throws ProcessException on problems with inner browser
	 */
	public String processAllReturningText(final String s) throws ProcessException {
		
		
		 
		getWpValues(s, tab);
		LOG.debug(tab);
		return s;
	}
	
	
	/**
	 * 
	 * @param text where to search
	 * @param tab tabel with required values
	 */
	 private void getWpValues(final String text, Hashtable<String, String> tab) {
		 
		 String [] tParts = text.split("\n");
//		 System.out.println(tParts.length);
		 for (int i = 0; i < tParts.length; i++) {
			if (tParts[i].indexOf("wpEditToken") > 0) {
				// \<input type='hidden' value=\"(.*?)\" name=\"wpEditToken\"
				int begin = tParts[i].indexOf("value") + 7;
				int end = tParts[i].indexOf("name") - 2;
//				System.out.println(line.substring(begin, end));
//				System.out.println("read wp token:" + tParts[i]);
				tab.put("wpEditToken", tParts[i].substring(begin, end));
			
			} else if (tParts[i].indexOf("wpEdittime") > 0) {
				// value="(\d+)" name=["\']wpEdittime["\']
				int begin = tParts[i].indexOf("value") + 7;
				int end = tParts[i].indexOf("name") - 2;
//				System.out.println( "read wp edit: " + tParts[i].substring(begin, end));
				
				tab.put("wpEdittime", tParts[i].substring(begin, end));

				
			
			} else if (tParts[i].indexOf("wpStarttime") > 0) {
				// value="(\d+)" name=["\']wpStarttime["\']
				int begin = tParts[i].indexOf("value") + 7;
				int end = tParts[i].indexOf("name") - 2;
//				System.out.println("read wp start:" + tParts[i]);
				
				tab.put("wpStarttime", tParts[i].substring(begin, end));
			
			}
		 }
			
	 }
	

}
