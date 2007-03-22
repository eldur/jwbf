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
package net.sourceforge.jwbf.bots;


import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.actions.http.Action;
import net.sourceforge.jwbf.actions.http.ActionException;
import net.sourceforge.jwbf.actions.http.HttpActionClient;

import org.apache.commons.httpclient.HttpClient;

/**
 * 
 * @author Thomas Stock
 *
 */

public abstract class HttpBot {

	private HttpActionClient cc;
	
	/**
	 * protected because abstract.
	 *
	 */
	protected HttpBot() {
		JWBF.prepareLogger();
	}
	/**
	 * 
	 * @param client if you whant to add some specials
	 * @param u like http://www.yourOwnWiki.org/w/index.php
	 * 
	 */
	protected void setConnection(final HttpClient client, final URL u) {
			client.getParams().setParameter("http.useragent", "JWBF " + JWBF.VERSION);
			client.getHostConfiguration().setHost(u.getHost(), u.getPort(), u.getProtocol());
			cc = new HttpActionClient(client, u.getPath());
			

	}
	/**
	 * 
	 * @param a a
	 * @throws ActionException on problems
	 * @return text
	 */
	public final String performAction(final Action a) throws ActionException {
		return cc.performAction(a);
	}
	
	/**
	 * 
	 * @param hostUrl like http://www.yourOwnWiki.org/wiki/index.php
	 */
	protected void setConnection(final String hostUrl) {
		try {
			HttpClient client = new HttpClient();
			client.getParams().setParameter("http.useragent", "JWBF " + JWBF.VERSION);
			setConnection(client, new URL(hostUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param hostUrl like http://www.yourOwnWiki.org/wiki/index.php
	 */
	protected void setConnection(final URL hostUrl) {
		
		HttpClient client = new HttpClient();
			setConnection(client, hostUrl);
		
	}

}
