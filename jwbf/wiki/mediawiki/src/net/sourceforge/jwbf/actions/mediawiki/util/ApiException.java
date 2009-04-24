package net.sourceforge.jwbf.actions.mediawiki.util;

import net.sourceforge.jwbf.actions.util.ProcessException;


public class ApiException extends ProcessException {

	
	
	public ApiException(String code, String value) {
		
		super("API ERROR CODE: " + code 
				+ " VALUE: " + value);
	}


	

}
