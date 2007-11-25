package net.sourceforge.jwbf.actions.http;

import java.util.List;
import java.util.Vector;

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
		return text;
	}

	
}
