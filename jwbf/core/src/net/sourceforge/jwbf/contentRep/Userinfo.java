package net.sourceforge.jwbf.contentRep;

import java.util.Collection;
/**
 * Information about the current user.
 * @author Thomas Stock
 */
public interface Userinfo {
	/**
	 * 
	 * @return the rights, like "read, write, ..."
	 */
	Collection<String> getRights();
	/**
	 * 
	 * @return the groups, like "user, bot, ..." 
	 */
	Collection<String> getGroups();

	/**
	 * 
	 * @return the
	 */
	String getUsername();
	
	
}
