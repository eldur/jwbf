package net.sourceforge.jwbf.contentRep.mw;

import net.sourceforge.jwbf.contentRep.Version;

public class Siteinfo {

	private String mainpage = "";
	private String base = "";
	private String sitename = "";
	private String generator = "";
	private String theCase = "";
	private String rights = "";

	public Siteinfo() {

	}

	public String getMainpage() {
		return mainpage;
	}

	public void setMainpage(String mainpage) {
		this.mainpage = mainpage;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getSitename() {
		return sitename;
	}

	public void setSitename(String sitename) {
		this.sitename = sitename;
	}

	/**
	 * 
	 * @return the MediaWiki Generator
	 */
	public String getGenerator() {
		return generator;
	}

	public Version getVersion() {
		if (getGenerator().contains("1.9.")) {
			return Version.MW1_9;
		} else if (getGenerator().contains("1.10.")) {
			return Version.MW1_10;
		} else if (getGenerator().contains("1.11.")) {
			return Version.MW1_11;
		} else if (getGenerator().contains("1.12")) { // actual Wikipedia
			return Version.MW_WIKIPEDIA;
		} else {
			return Version.UNKNOWN;
		}
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

	public String getCase() {
		return theCase;
	}

	public void setCase(String theCase) {
		this.theCase = theCase;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String toString() {
		String temp = "This is " + getSitename() + " @ " + getGenerator()
				+ " (" + getVersion() + ".x)";

		return temp;
	}

}
