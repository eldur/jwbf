package net.sourceforge.jwbf.contentRep.mw;

import java.util.Collection;

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
