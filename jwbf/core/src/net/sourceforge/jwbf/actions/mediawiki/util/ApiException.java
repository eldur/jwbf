package net.sourceforge.jwbf.actions.mediawiki.util;

import net.sourceforge.jwbf.actions.util.ProcessException;


public class ApiException extends ProcessException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -959971173922381579L;

	public ApiException(String code, String value) {
		super("API ERROR CODE: " + code 
		+ " VALUE: " + value);
	}
	

}
