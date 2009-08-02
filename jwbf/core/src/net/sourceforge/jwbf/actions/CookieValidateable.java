package net.sourceforge.jwbf.actions;

import java.util.Map;

import net.sourceforge.jwbf.actions.util.CookieException;
import net.sourceforge.jwbf.actions.util.HttpAction;

/**
 * Use this interface to handle cookies in your action. 
 * @author Thomas Stock
 *
 */
public interface CookieValidateable {
	/**
	 * 
	 * @param cs a
	 * @param hm a
	 * @throws CookieException on problems with cookies
	 */
	void validateReturningCookies(final Map<String, String> cs, HttpAction hm) throws CookieException;

}
