package net.sourceforge.jwbf.actions;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jwbf.actions.util.HttpAction;

public class Post implements HttpAction {

	private final String req;
	private Map<String, String> params = new HashMap<String, String>();
	private final String charset;
	
	public Post(String req, String charset) {
		this.req = req;
		this.charset = charset;
	}
	/**
	 * Use utf-8 as default charset
	 * @param req a
	 */
	public Post(String req) {
		this(req, "utf-8");
	}
	

	public void addParam(String key, String value) {
		params.put(key, value);
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public String getRequest() {
		return req;
	}

	public String getCharset() {
		return charset;
	}
	
	
}
