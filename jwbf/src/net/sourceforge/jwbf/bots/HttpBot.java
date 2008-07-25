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

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.actions.ContentProcessable;
import net.sourceforge.jwbf.actions.GetPage;
import net.sourceforge.jwbf.actions.HttpActionClient;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

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
				"JWBF " + JWBF.getVersion());
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
	 * @throws ProcessException on problems in the subst of ContentProcessable 
	 */
	public final synchronized String performAction(final ContentProcessable a)
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

		client.getParams().setParameter("http.useragent",
				"JWBF " + JWBF.getVersion());
		setConnection(client, new URL(hostUrl));

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
			return cc.get(new GetMethod(u));
		} catch (ProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
