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
import java.util.Hashtable;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.actions.http.Action;
import net.sourceforge.jwbf.contentRep.mw.EditContentAccessable;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * 
 * @author Thomas Stock
 *
 */
public class PostModifyContent extends Action {

	/**
	 * 
	 * @param a the
	 * @param tab internal value set
	 */
	public PostModifyContent(final EditContentAccessable a,
			final Hashtable<String, String> tab) {

		String uS = "";
		try {
			uS = "/index.php?title=" + URLEncoder.encode(a.getLabel(), JWBF.charset)
					+ "&action=submit";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		NameValuePair action = new NameValuePair("wpSave", "Save");
		NameValuePair wpStarttime = new NameValuePair("wpStarttime", tab
				.get("wpStarttime"));
		NameValuePair wpEditToken = new NameValuePair("wpEditToken", tab
				.get("wpEditToken"));
		NameValuePair wpEdittime = new NameValuePair("wpEdittime", tab
				.get("wpEdittime"));

		NameValuePair wpTextbox = new NameValuePair("wpTextbox1", a.getText());
		
		String editSummaryText = a.getEditSummary();
		if (editSummaryText.length() > 200) {
			editSummaryText = editSummaryText.substring(0, 200);
		}
		
		NameValuePair wpSummary = new NameValuePair("wpSummary", editSummaryText);
		
		
		NameValuePair wpMinoredit = new NameValuePair();
		
		if (a.isMinorEdit()) {
			wpMinoredit.setValue("1");
			wpMinoredit.setName("wpMinoredit");
		}
		
		
		
		
		log.info("WRITE: " + a.getLabel());
		PostMethod pm = new PostMethod(uS);
		pm.getParams().setContentCharset(JWBF.charset);

		pm.setRequestBody(new NameValuePair[] { action, wpStarttime,
				wpEditToken, wpEdittime, wpTextbox, wpSummary, wpMinoredit });
		msgs.add(pm);
	}

}
