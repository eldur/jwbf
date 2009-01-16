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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.mw.HttpAction;
import net.sourceforge.jwbf.actions.mw.MediaWiki;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.CookieException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
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
		

		
		String out = "";
		while (a.hasMoreMessages()) {
			
			HttpMethod e = null;
				try {
					
					HttpAction ha = a.getNextMessage();
					
					if (ha instanceof Get) {
						e = new GetMethod(ha.getRequest());	
						if (path.length() > 1) {

							e.setPath(path + e.getPath());
							LOG.debug("path is: " + e.getPath());

						}
						
						// do get
						out = get(e, a, ha);
					} else if (ha instanceof Post) {
						Post p = (Post) ha;
						e = new PostMethod(p.getRequest());	
						if (path.length() > 1) {

							e.setPath(path + e.getPath());
							LOG.debug("path is: " + e.getPath());

						}
						Iterator<String> keys = p.getParams().keySet().iterator();
						int count = p.getParams().size();
						NameValuePair[] val = new NameValuePair[count];
						int i = 0;
						while (keys.hasNext()) {
							String key = (String) keys.next();
							val[i++] = new NameValuePair(key, p.getParams().get(key));
						}

						((PostMethod) e).setRequestBody(val);
						if (ha instanceof FilePost) {
							LOG.debug("is filepost");
							FilePost px = (FilePost) ha;
							
							
							
							// add multipart elements
							
							Part[] parts;
							
							Iterator<String> partKeys =  px.getParts().keySet().iterator();
							int partCount = px.getParts().size();
							parts = new Part[partCount];
							
							
							int j = 0;
							while (partKeys.hasNext()) {
								String key = (String) partKeys.next();
								Object po = px.getParts().get(key);
								if (po instanceof String) {
									parts[j++] = new StringPart(key, (String) po);
								} else if (po instanceof File) {
									parts[j++] = new FilePart(key, (File) po);
								}
								
							}
							
							((PostMethod) e).setRequestEntity(new MultipartRequestEntity(parts, e
									.getParams()));
							
							
							
						}
						// do post
						out = post(e, a, ha);
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
	protected String post(HttpMethod authpost, ContentProcessable cp, HttpAction ha)
			throws IOException, ProcessException,
			CookieException {
		showCookies(client);

		authpost.getParams().setParameter("http.protocol.content-charset",
				MediaWiki.getCharset());
		authpost.getParams().setContentCharset(MediaWiki.getCharset());
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
		
		out = cp.processReturningText(out, ha);
		
		cp.validateReturningCookies(client.getState().getCookies(), ha);
		
	

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
	protected String get(HttpMethod authgets, ContentProcessable cp, HttpAction ha)
			throws IOException, CookieException, ProcessException {
		showCookies(client);
		String out = "";
		authgets.getParams().setParameter("http.protocol.content-charset",
				MediaWiki.getCharset());
//		System.err.println(authgets.getParams().getParameter("http.protocol.content-charset"));
		
		client.executeMethod(authgets);
		cp.validateReturningCookies(client.getState().getCookies(), ha);
		LOG.debug(authgets.getURI());
		LOG.debug("GET: " + authgets.getStatusLine().toString());

		out = authgets.getResponseBodyAsString();
		
		out = cp.processReturningText(out, ha);
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
	 * @param ha
	 *            a
	 * @return a returning message, not null
	 * @throws IOException on problems
	 * @throws CookieException on problems
	 * @throws ProcessException on problems
	 */
	public byte[] get(Get ha)
			throws IOException, CookieException, ProcessException {
		showCookies(client);
		
		GetMethod authgets = new GetMethod(ha.getRequest());
		byte[] out = null;
		authgets.getParams().setParameter("http.protocol.content-charset",
				MediaWiki.getCharset());
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

