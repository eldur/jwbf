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

package net.sourceforge.jwbf.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.CookieException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 * The main interaction class.
 * 
 * @author Thomas Stock
 * 
 */
public class HttpActionClient {

	private HttpClient client;

	private String path = "";

	private static final Logger LOG = Logger.getLogger(HttpActionClient.class);

	/**
	 * 
	 * @param client
	 *            a
	 * @param path
	 *            like "/w/"
	 */
	public HttpActionClient(final HttpClient client, final String path) {
		/*
		 * see for docu
		 * http://jakarta.apache.org/commons/httpclient/preference-api.html
		 */

		this.client = client;
//		this.client.getParams().setParameter("http.protocol.content-charset",
//		 "UTF-8");
		
		if (path.length() > 1) {
			this.path = path.substring(0, path.lastIndexOf("/"));
		}

	}

	/**
	 * 
	 * @param client
	 *            a
	 */
	public HttpActionClient(final HttpClient client) {
		this(client, "");

	}

	/**
	 * 
	 * @param a
	 *            a
	 * @return message, never null
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on inner problems
	 */
	public String performAction(ContentProcessable a) throws ActionException, ProcessException {

		List<HttpMethod> msgs = a.getMessages();
		String out = "";
		Iterator<HttpMethod> it = msgs.iterator();
		while (it.hasNext()) {
			HttpMethod e = it.next();
			if (path.length() > 1) {

				e.setPath(path + e.getPath());
				LOG.debug("path is: " + e.getPath());

			}
			try {
				if (e instanceof GetMethod) {

					out = get(e, a);

				} else {
					out = post(e, a);
				}
			} catch (IOException e1) {
				throw new ActionException(e1);
			}

		}
		return out;

	}

	/**
	 * Process a POST Message.
	 * 
	 * @param authpost
	 *            a
	 * @param cp
	 *            a
	 * @return a returning message, not null
	 * @throws IOException on problems
	 * @throws ProcessException on problems
	 * @throws CookieException on problems
	 */
	protected String post(HttpMethod authpost, ContentProcessable cp)
			throws IOException, ProcessException,
			CookieException {
		showCookies(client);
		authpost.getParams().setParameter("http.protocol.content-charset",
				MediaWikiBot.CHARSET);
		String out = "";

		client.executeMethod(authpost);
		

		// Header locationHeader = authpost.getResponseHeader("location");
		// if (locationHeader != null) {
		// authpost.setRequestHeader(locationHeader) ;
		// }

		

		// Usually a successful form-based login results in a redicrect to
		// another url
		
		int statuscode = authpost.getStatusCode();
		if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY)
				|| (statuscode == HttpStatus.SC_MOVED_PERMANENTLY)
				|| (statuscode == HttpStatus.SC_SEE_OTHER)
				|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
			Header header = authpost.getResponseHeader("location");
			if (header != null) {
				String newuri = header.getValue();
				if ((newuri == null) || (newuri.equals(""))) {
					newuri = "/";
				}
				LOG.debug("Redirect target: " + newuri);
				GetMethod redirect = new GetMethod(newuri);

				client.executeMethod(redirect);
				LOG.debug("Redirect: " + redirect.getStatusLine().toString());
				// release any connection resources used by the method
				authpost.releaseConnection();
				authpost = redirect;
			}
		}
		
		out = authpost.getResponseBodyAsString();
		out = cp.processReturningText(out, authpost);

		cp.validateReturningCookies(client.getState().getCookies(), authpost);
		
	

		authpost.releaseConnection();
		LOG.debug(authpost.getURI() + " || " + "POST: "
				+ authpost.getStatusLine().toString());
		return out;
	}

	/**
	 * Process a GET Message.
	 * 
	 * @param authgets
	 *            a
	 * @param cp
	 *            a
	 * @return a returning message, not null
	 * @throws IOException on problems
	 * @throws CookieException on problems
	 * @throws ProcessException on problems
	 */
	protected String get(HttpMethod authgets, ContentProcessable cp)
			throws IOException, CookieException, ProcessException {
		showCookies(client);
		String out = "";
		authgets.getParams().setParameter("http.protocol.content-charset",
				MediaWikiBot.CHARSET);
//		System.err.println(authgets.getParams().getParameter("http.protocol.content-charset"));
		
		client.executeMethod(authgets);
		cp.validateReturningCookies(client.getState().getCookies(), authgets);
		LOG.debug(authgets.getURI());
		LOG.debug("GET: " + authgets.getStatusLine().toString());

		out = authgets.getResponseBodyAsString();
		
		out = cp.processReturningText(out, authgets);
		// release any connection resources used by the method
		authgets.releaseConnection();
		int statuscode = authgets.getStatusCode();

		if (statuscode == HttpStatus.SC_NOT_FOUND) {
			LOG.warn("Not Found: " + authgets.getQueryString());

			throw new FileNotFoundException(authgets.getQueryString());
		}

		return out;
	}
	
	/**
	 * Process a GET Message.
	 * 
	 * @param authgets
	 *            a
	 * @param cp
	 *            a
	 * @return a returning message, not null
	 * @throws IOException on problems
	 * @throws CookieException on problems
	 * @throws ProcessException on problems
	 */
	public byte[] get(HttpMethod authgets)
			throws IOException, CookieException, ProcessException {
		showCookies(client);
		byte[] out = null;
		authgets.getParams().setParameter("http.protocol.content-charset",
				MediaWikiBot.CHARSET);
//		System.err.println(authgets.getParams().getParameter("http.protocol.content-charset"));
		
		client.executeMethod(authgets);
		LOG.debug(authgets.getURI());
		LOG.debug("GET: " + authgets.getStatusLine().toString());

		out = authgets.getResponseBody();
		
		// release any connection resources used by the method
		authgets.releaseConnection();
		int statuscode = authgets.getStatusCode();

		if (statuscode == HttpStatus.SC_NOT_FOUND) {
			LOG.warn("Not Found: " + authgets.getQueryString());

			throw new FileNotFoundException(authgets.getQueryString());
		}

		return out;
	}

	/**
	 * send the cookies to the logger.
	 * 
	 * @param client
	 *            a
	 */
	protected void showCookies(HttpClient client) {
		Cookie[] cookies = client.getState().getCookies();
		if (cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				LOG.trace("cookie: " + cookies[i].toString());
			}
		}
	}

}

