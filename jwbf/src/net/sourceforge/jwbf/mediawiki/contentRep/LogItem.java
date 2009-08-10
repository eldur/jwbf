package net.sourceforge.jwbf.mediawiki.contentRep;

public class LogItem {
	private String title;
	private String type;
	private String user;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	@Override
	public String toString() {

		return "* " + getTitle() + " was " + getType() + " by " + getUser();
	}

}
