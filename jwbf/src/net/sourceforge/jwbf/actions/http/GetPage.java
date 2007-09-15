package net.sourceforge.jwbf.actions.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class GetPage implements ContentProcessable {

	private List<HttpMethod> msgs = new Vector<HttpMethod>();
	
	private String text = "";
	
	public GetPage(String u) {		
		msgs.add(new GetMethod(u));
		
	}
	
	public List<HttpMethod> getMessages() {
		return msgs;
	}

	public String processReturningText(String s, HttpMethod hm) throws ProcessException {
		text = s;
		return s;
	}

	public void validateReturningCookies(Cookie[] cs, HttpMethod hm)
			throws CookieException {
		// TODO Auto-generated method stub

	}

	public String getText() {
		return encodeUtf8(text);
	}
	/**
	 * changes to mediawiki default encoding.
	 * @param s a
	 * @return encoded s
	 */
	private String encodeUtf8(final String s) {
		
		try {
			return new String(s.getBytes(), MediaWikiBot.CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NullPointerException npe) {
			return s;
		}
		return s;
		
//		java 1.6 version
//		return new String(s.getBytes(), Charset.forName("UTF-8"));
	}
	
}
