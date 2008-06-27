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
package net.sourceforge.jwbf.actions.mw.old;

import java.util.Hashtable;

import net.sourceforge.jwbf.actions.mw.util.MWAction;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;


/**
 * @author Thomas Stock
 * @deprecated
 */
public class PostDelete extends MWAction {

	/**
	 * 
	 * @param label of the article
	 * @param tab with contains environment variable "wpEditToken"
	 * @deprecated
	 */
	public PostDelete(final String label, Hashtable<String, String> tab) {
		
		NameValuePair action = new NameValuePair("wpConfirmB", "Delete Page");
		// this value is preseted
		NameValuePair wpReason = new NameValuePair("wpReason", "hier der Grund");
		wpReason.setName("backdraft");
		
		NameValuePair wpEditToken = new NameValuePair("wpEditToken", tab
				.get("wpEditToken"));

		PostMethod pm = new PostMethod(
				"/index.php?title=" + label + "&action=delete");

		pm.setRequestBody(new NameValuePair[] { action, wpReason, wpEditToken });
		pm.getParams().setContentCharset(MediaWikiBot.CHARSET);
		msgs.add(pm);
		
	}
	
}
