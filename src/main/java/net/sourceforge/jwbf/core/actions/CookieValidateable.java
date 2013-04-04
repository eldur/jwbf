package net.sourceforge.jwbf.core.actions;

import java.util.Map;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

/**
 * Use this interface to handle cookies in your action.
 * 
 * @author Thomas Stock
 * 
 */
public interface CookieValidateable {

  void validateReturningCookies(final Map<String, String> cookies, HttpAction action);

}
