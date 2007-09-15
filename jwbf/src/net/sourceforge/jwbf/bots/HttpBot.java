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
 * Philipp Kohl 
 */
package net.sourceforge.jwbf.bots;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.actions.http.ActionException;
import net.sourceforge.jwbf.actions.http.ContentProcessable;
import net.sourceforge.jwbf.actions.http.GetPage;
import net.sourceforge.jwbf.actions.http.HttpActionClient;
import net.sourceforge.jwbf.actions.http.ProcessException;

import org.apache.commons.httpclient.HttpClient;

/**
 * 
 * @author Thomas Stock
 * 
 */

public abstract class HttpBot {

	private HttpActionClient cc;

	private HttpClient client;

	/**
	 * protected because abstract.
	 * 
	 */
	protected HttpBot() {
		client = new HttpClient();
		
	}

	/**
	 * 
	 * @param client
	 *            if you whant to add some specials
	 * @param u
	 *            like http://www.yourOwnWiki.org/w/index.php
	 * 
	 */
	protected final void setConnection(final HttpClient client, final URL u) {

		this.client = client;
		client.getParams().setParameter("http.useragent",
				"JWBF " + JWBF.VERSION);
		client.getHostConfiguration().setHost(u.getHost(), u.getPort(),
				u.getProtocol());
		cc = new HttpActionClient(client, u.getPath());

	}

	/**
	 * 
	 * @param a
	 *            a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @return text
	 * @throws ProcessException 
	 */
	public final String performAction(final ContentProcessable a) throws ActionException, ProcessException {
		return cc.performAction(a);
	}

	/**
	 * 
	 * @param hostUrl
	 *            like http://www.yourOwnWiki.org/wiki/
	 */
	protected final void setConnection(final String hostUrl) {
		try {
			client.getParams().setParameter("http.useragent",
					"JWBF " + JWBF.VERSION);
			setConnection(client, new URL(hostUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Simple method to get plain HTML or XML data e.g. from custom 
	 * specialpages or xml newsfeeds.
	 * @param u url like index.php?title=Main_Page
	 * @return HTML content
	 * @throws ActionException on any requesing problems
	 * @supportedBy 
	 */
	public String getPage(String u) throws ActionException {
		
		GetPage gp = new GetPage(u);
		
		try {
			performAction(gp);
		} catch (ProcessException e) {
			e.printStackTrace();
		}
		
		return gp.getText();
	}

	/**
	 * 
	 * @return a
	 */
	public final HttpClient getClient() {
		return client;
	}

	/**
	 * 
	 * @param hostUrl
	 *            like http://www.yourOwnWiki.org/wiki/
	 */
	protected final void setConnection(final URL hostUrl) {
		setConnection(client, hostUrl);

	}
	


}
