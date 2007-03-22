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
package net.sourceforge.jwbf.actions.http.mw;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.jwbf.actions.http.Action;

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * 
 * @author Thomas Stock
 * TODO
 * 
 */
public class GetPageContent extends Action {

	/**
	 * 
	 * @param articlename
	 *            the
	 */
	public GetPageContent(final String articlename) {
		log.info("READ: " + articlename);
		String uS = "";
		try {
			uS = "/index.php?title=" + URLEncoder.encode(articlename, "UTF-8")
					+ "&action=raw&ctype=text/plain&dontcountme=s";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		msgs.add(new GetMethod(uS));
	}
	

	/**
	 * @see Action#processAllReturningText(String)
	 * @param s
	 *            returning text
	 * @return text
	 */
	public String processAllReturningText(final String s) {
		try {
			return new String(s.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
//		java 1.6 version
		// TODO check if old utf works like new
//		return new String(s.getBytes(), Charset.forName("UTF-8"));
	}

}
