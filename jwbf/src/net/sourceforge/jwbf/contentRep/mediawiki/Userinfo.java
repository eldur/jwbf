package net.sourceforge.jwbf.contentRep.mediawiki;

import java.util.Collection;
/**
 * Information about the current user.
 * @author Thomas Stock
 *
 */
public class Userinfo {

	private final Collection<String> rights;
	private final Collection<String> groups;
	private final String username;
	
	public Userinfo(String username, boolean blockinfo, boolean hasmsg, Collection<String> groups, Collection<String> rights) {
		this.username = username;
		this.rights = rights;
		this.groups = groups;
	}
	
	public Collection<String> getRights() {
		return rights;
	}
	public Collection<String> getGroups() {
		return groups;
	}

	public String getUsername() {
		return username;
	}
}
