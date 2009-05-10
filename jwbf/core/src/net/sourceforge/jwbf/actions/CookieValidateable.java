package net.sourceforge.jwbf.actions;

import java.util.Map;

import net.sourceforge.jwbf.actions.util.CookieException;
import net.sourceforge.jwbf.actions.util.HttpAction;

public interface CookieValidateable {

	
	/**
	 * 
	 * @param cs a
	 * @param hm a
	 * @throws CookieException on problems with cookies
	 * TODO RM the org apache cookie from this interface
	 */
	void validateReturningCookies(final Map<String, String> cs, HttpAction hm) throws CookieException;

}
