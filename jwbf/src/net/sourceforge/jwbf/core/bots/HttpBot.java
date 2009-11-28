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
package net.sourceforge.jwbf.core.bots;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.GetPage;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;

/**
 * 
 * @author Thomas Stock
 * 
 */

public class HttpBot {

	private HttpActionClient cc;


	/**
	 * Design for extension.
	 * @param url of the host
	 */
	protected HttpBot(final String url) {
		try {
			setConnection(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Design for extension.
	 * @param cc a
	 */
	protected HttpBot(HttpActionClient cc) {
		this.cc = cc;
	}
	/**
	 * Design for extension.
	 * @param url of the host
	 */
	protected HttpBot(final URL url) {
			setConnection(url);
	}
	
	/**
	 * Returns a {@link HttpBot} which supports only its basic methods. 
	 * Use {@link #getPage(String)} for an basic read of content.
	 *  
	 * @return a
	 */
	public static HttpBot getInstance() {
		
		HttpActionClient cc = null;
			try {
				cc = new HttpActionClient(new URL("http://localhost/"));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
	
			return new HttpBot(cc);
	}
	/**
	 * 
	 * @param client
	 *            if you whant to add some specials
	 * 
	 */
	public final void setConnection(final HttpActionClient client) {
		cc = client;
	}

	
	public final String getHostUrl() {
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
	public final String getPage(String u) throws ActionException {

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
		return cc;
	}

	/**
	 * 
	 * @param hostUrl
	 *            like http://www.yourOwnWiki.org/wiki/
	 */
	protected final void setConnection(final URL hostUrl) {
		setConnection(new HttpActionClient(hostUrl));

	}


}
