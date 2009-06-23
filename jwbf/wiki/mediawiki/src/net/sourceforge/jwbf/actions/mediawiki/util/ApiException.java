package net.sourceforge.jwbf.actions.mediawiki.util;

import net.sourceforge.jwbf.actions.util.ProcessException;

/**
 * 
 * @author Thomas Stock
 *
 */
public class ApiException extends ProcessException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -959971173922381579L;

	/**
	 * 
	 * @param code a
	 * @param value a
	 */
	public ApiException(String code, String value) {
		
		super("API ERROR CODE: " + code 
				+ " VALUE: " + value);
	}


	

}
