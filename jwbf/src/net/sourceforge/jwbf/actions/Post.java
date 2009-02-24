package net.sourceforge.jwbf.actions;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jwbf.actions.util.HttpAction;

public class Post implements HttpAction {

	final private String req;
	private Map<String, String> params = new HashMap<String, String>();
	
	public Post(String req) {
		this.req = req;
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
	
	
}
