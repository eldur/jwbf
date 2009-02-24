package net.sourceforge.jwbf.actions.mediawiki.util;

import net.sourceforge.jwbf.actions.util.ProcessException;


public class ApiException extends ProcessException {

	private String msg = "";
	
	public ApiException(String code, String value) {
		msg = "API ERROR CODE: " + code 
		+ " VALUE: " + value;
	}

	@Override
	public String getMessage() {
		return msg;
	}
	

}
