package net.sourceforge.jwbf.contentRep.mw;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jwbf.contentRep.Version;

public class Siteinfo {

	private String mainpage = "";
	private String base = "";
	private String sitename = "";
	private String generator = "";
	private String theCase = "";
	private String rights = "";

	private Map<Integer,String> namespaces=new HashMap<Integer,String>();
	private Map<String,String> interwiki=new HashMap<String,String>();
	
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

	public void addNamespace(Integer id, String name) {
		namespaces.put(id,name);
		
	}

	public Map<Integer, String> getNamespaces() {
		return Collections.unmodifiableMap(namespaces);
	}

	public void addInterwiki(String prefix, String name) {
		interwiki.put(prefix, name);
	}
	public Map getInterwikis(){
		return Collections.unmodifiableMap(interwiki);
	}

}
