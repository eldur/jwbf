package net.sourceforge.jwbf.actions;

import net.sourceforge.jwbf.actions.mw.HttpAction;

public class Get implements HttpAction {

	private String req = "";
	
	public Get(String req) {
		this.req = req;
	}
	
	public String getRequest() {
		return req;
	}
	
}
