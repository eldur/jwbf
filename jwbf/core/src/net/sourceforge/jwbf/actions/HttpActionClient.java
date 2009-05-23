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
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.CookieException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;

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

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 
	 * @param client
	 *            a
	 * @param url
	 *            like "http://host/of/wiki/"
	 */
	public HttpActionClient(final HttpClient client, final URL url) {
		/*
		 * see for docu
		 * http://jakarta.apache.org/commons/httpclient/preference-api.html
		 */

		
//		this.client.getParams().setParameter("http.protocol.content-charset",
//		 "UTF-8");
		
		if (url.getPath().length() > 1) {
			this.path = url.getPath().substring(0, url.getPath().lastIndexOf("/"));
		}

		client.getParams().setParameter("http.useragent",
				"JWBF " + JWBF.getVersion(getClass()));
		client.getHostConfiguration().setHost(url.getHost(), url.getPort(),
				url.getProtocol());
		this.client = client;
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
	public synchronized String performAction(ContentProcessable a)
			throws ActionException, ProcessException {

		String out = "";
		while (a.hasMoreMessages()) {
			
			HttpMethod e = null;
			try {

				HttpAction ha = a.getNextMessage();
				
				
				
				if (ha instanceof Get) {
					e = new GetMethod(ha.getRequest());
					if (path.length() > 1) {
						e.setPath(path + e.getPath());
					}

					// do get
					out = get(e, a, ha);
				} else if (ha instanceof Post) {
					Post p = (Post) ha;
					e = new PostMethod(p.getRequest());
					if (path.length() > 1) {

						e.setPath(path + e.getPath());
						
//						log.debug("path is: " + e.getPath());

					}
					Iterator<String> keys = p.getParams().keySet().iterator();
					int count = p.getParams().size();
					NameValuePair[] val = new NameValuePair[count];
					int i = 0;
					while (keys.hasNext()) {
						String key = (String) keys.next();
						val[i++] = new NameValuePair(key, p.getParams()
								.get(key));
					}

					((PostMethod) e).setRequestBody(val);
					if (ha instanceof FilePost) {
						log.debug("is filepost");
						FilePost px = (FilePost) ha;

						// add multipart elements

						Part[] parts;

						Iterator<String> partKeys = px.getParts().keySet()
								.iterator();
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

						((PostMethod) e)
								.setRequestEntity(new MultipartRequestEntity(
										parts, e.getParams()));

					}
					// do post
					out = post(e, a, ha);
				}
				

			} catch (IOException e1) {
				throw new ActionException(e1, getClass());
			} catch (IllegalArgumentException e2) {
				throw new ActionException(e2, getClass());
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
	private String post(HttpMethod authpost, ContentProcessable cp, HttpAction ha)
			throws IOException, ProcessException,
			CookieException {
		debug(authpost, ha);
		showCookies(client);
		
		authpost.getParams().setParameter("http.protocol.content-charset",
				ha.getCharset());
		authpost.getParams().setContentCharset(ha.getCharset());
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
				
				GetMethod redirect = new GetMethod(newuri);

				client.executeMethod(redirect);
				if (!redirect.getStatusLine().toString().contains("200")
						&& log.isDebugEnabled()) {
					log.debug("Redirect target: " + newuri);
					log.debug("Redirect: "
							+ redirect.getStatusLine().toString());
				}
				// release any connection resources used by the method
				authpost.releaseConnection();
				authpost = redirect;
			}
		}
		
		out = authpost.getResponseBodyAsString();
		
		out = cp.processReturningText(out, ha);
		if (cp instanceof CookieValidateable)
			((CookieValidateable) cp).validateReturningCookies(cookieTransform(client.getState().getCookies()), ha);
		
	

		authpost.releaseConnection();
//		log.debug(authpost.getURI() + " || " + "POST: "
//				+ authpost.getStatusLine().toString());
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
	private String get(HttpMethod authgets, ContentProcessable cp, HttpAction ha)
			throws IOException, CookieException, ProcessException {
		showCookies(client);
		debug(authgets, ha);
		String out = "";
		authgets.getParams().setParameter("http.protocol.content-charset",
				ha.getCharset());
//		System.err.println(authgets.getParams().getParameter("http.protocol.content-charset"));
		
		client.executeMethod(authgets);
		if (cp instanceof CookieValidateable)
			((CookieValidateable) cp).validateReturningCookies(cookieTransform(client.getState().getCookies()), ha);
//		log.debug(authgets.getURI());
		if (!authgets.getStatusLine().toString().contains("200") && log.isDebugEnabled()) 
			log.debug("GET: " + authgets.getStatusLine().toString());

		out = authgets.getResponseBodyAsString();
		
		out = cp.processReturningText(out, ha);
		// release any connection resources used by the method
		authgets.releaseConnection();
		int statuscode = authgets.getStatusCode();

		if (statuscode == HttpStatus.SC_NOT_FOUND) {
			log.warn("Not Found: " + authgets.getQueryString());

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
				ha.getCharset());
//		System.err.println(authgets.getParams().getParameter("http.protocol.content-charset"));
		
		client.executeMethod(authgets);
		log.debug(authgets.getURI());
		log.debug("GET: " + authgets.getStatusLine().toString());

		out = authgets.getResponseBody();
		
		// release any connection resources used by the method
		authgets.releaseConnection();
		int statuscode = authgets.getStatusCode();

		if (statuscode == HttpStatus.SC_NOT_FOUND) {
			log.warn("Not Found: " + authgets.getQueryString());

			throw new FileNotFoundException(authgets.getQueryString());
		}

		return out;
	}

	private Map<String, String> cookieTransform(Cookie [] ca) {
		Map<String, String> m = new HashMap<String, String>();
		for (int i = 0; i < ca.length; i++) {
			m.put(ca[i].getName(), ca[i].getValue());
		}
		return m;
	}
	/**
	 * send the cookies to the logger.
	 * 
	 * @param client
	 *            a
	 *            @deprecated is a bit too chatty
	 */
	private void showCookies(HttpClient client) {
		Cookie[] cookies = client.getState().getCookies();
		if (cookies.length > 0) {
			String cStr = "";
			for (int i = 0; i < cookies.length; i++) {
				cStr += cookies[i].toString() + ", ";
			}
			log.trace("cookie: {" + cStr + "}");
		}
	}

	
	private void debug(HttpMethod e, HttpAction ha) {
		if (log.isDebugEnabled()) {
			String epath = e.getPath();
			int sl = epath.lastIndexOf("/");
			epath = epath.substring(0, sl);
			String type = "";
			if (ha instanceof Post) {
				type = "(POST)";
			} else if (ha instanceof Get) {
				type = "(GET)";
			}
			log.debug("message " + type + " is: " 
					+  client.getHostConfiguration().getHostURL() 
					+ epath +  ha.getRequest());
		}
	}
	
	public String getHostUrl() {
		return client.getHostConfiguration().getHostURL();
	}
}

