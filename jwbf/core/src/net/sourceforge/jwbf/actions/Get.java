package net.sourceforge.jwbf.actions;

import net.sourceforge.jwbf.actions.util.HttpAction;

public class Get implements HttpAction {

	private final String req;
	private final String charset;
	
	public Get(String req, String charset) {
		this.req = req;
		this.charset = charset;
	}
	
	/**
	 * Use utf-8 as default charset
	 * @param req a
	 */
	public Get(String req) {
		this(req, "utf-8");
	}
	
	public String getRequest() {
		return req;
	}
	
	public String getCharset() {
		
		return charset;
	}
	
}
