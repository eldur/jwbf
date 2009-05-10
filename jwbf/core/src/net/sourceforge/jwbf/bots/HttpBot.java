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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.actions.ContentProcessable;
import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.GetPage;
import net.sourceforge.jwbf.actions.HttpActionClient;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;

import org.apache.commons.httpclient.HttpClient;

/**
 * 
 * @author Thomas Stock
 * 
 */

public class HttpBot {

	private HttpActionClient cc;
	private boolean init = true;

	/**
	 * protected because abstract.
	 * 
	 */
	protected HttpBot(final String url) {
		try {
			setConnection(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	protected HttpBot(HttpActionClient cc) {
		this.cc = cc;
	}

	protected HttpBot(final URL url) {
			setConnection(url);
	}
	
	private HttpBot() {

	}
	
	public static HttpBot getInstance() {
		return new HttpBot();
	}
	/**
	 * 
	 * @param client
	 *            if you whant to add some specials
	 * @param u
	 *            like http://www.yourOwnWiki.org/w/index.php
	 * 
	 */
	public final void setConnection(final HttpActionClient client) {
		cc = client;
	}

	
	public final String getHostUrl() {
		checkClient();
		return cc.getHostUrl();
	}
	/**
	 * 
	 * @param a
	 *            a
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @return text
	 * @throws ProcessException on problems in the subst of ContentProcessable 
	 */
	public synchronized String performAction(final ContentProcessable a)
			throws ActionException, ProcessException {
		return cc.performAction(a);
	}

	/**
	 * 
	 * @param hostUrl
	 *            base url of a wiki site to connect with; example:
	 *            http://www.yourOwnWiki.org/wiki/
	 * @throws MalformedURLException
	 *             if hostUrl does not represent a well-formed url
	 */
	protected final void setConnection(final String hostUrl)
			throws MalformedURLException {
		setConnection(new URL(hostUrl));

	}

	/**
	 * Simple method to get plain HTML or XML data e.g. from custom specialpages
	 * or xml newsfeeds.
	 * 
	 * @param u
	 *            url like index.php?title=Main_Page
	 * @return HTML content
	 * @throws ActionException
	 *             on any requesing problems
	 */
	public String getPage(String u) throws ActionException {

			try {
				URL url = new URL(u);
				
				setConnection(url.getProtocol() + "://" + url.getHost());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		
		GetPage gp = new GetPage(u);

		
		try {
			performAction(gp);
		} catch (ProcessException e) {
			e.printStackTrace();
		}

		return gp.getText();
	}
	
	/**
	 * Simple method to get plain HTML or XML data e.g. from custom specialpages
	 * or xml newsfeeds.
	 * 
	 * @param u
	 *            url like index.php?title=Main_Page
	 * @return HTML content
	 * @throws ActionException
	 *             on any requesing problems
	 */
	public final byte[] getBytes(String u) throws ActionException {

		
		checkClient();
		try {
			return cc.get(new Get(u));
		} catch (ProcessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @return a
	 */
	public final HttpActionClient getClient() {
		checkClient();
		return cc;
	}

	/**
	 * 
	 * @param hostUrl
	 *            like http://www.yourOwnWiki.org/wiki/
	 */
	protected final void setConnection(final URL hostUrl) {
		setConnection(new HttpActionClient(new HttpClient(), hostUrl));

	}

	private void checkClient() {
		if (cc == null && init) {
			init = false;
			try {
				cc = new HttpActionClient(new HttpClient(), new URL("http://localhost/"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
}
