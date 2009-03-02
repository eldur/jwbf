package net.sourceforge.jwbf.contentRep.mediawiki;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version;

import org.apache.log4j.Logger;


public class Siteinfo {

	private String mainpage = "";
	private String base = "";
	private String sitename = "";
	private String generator = "";
	private String theCase = "";

	private Map<Integer,String> namespaces=new HashMap<Integer,String>();
	private Map<String,String> interwiki=new HashMap<String,String>();
	private boolean writeApi;
	
	private static Logger log = Logger.getLogger(Siteinfo.class);
	
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
			return Version.MW1_09;
		} else if (getGenerator().contains("1.10.")) {
			return Version.MW1_10;
		} else if (getGenerator().contains("1.11.")) {
			return Version.MW1_11;
		} else if (getGenerator().contains("1.12")) {
			return Version.MW1_12;
		} else if (getGenerator().contains("1.13")) {
			return Version.MW1_13;
		} else if (getGenerator().contains("1.14")) {
			return Version.MW1_14;
		} else {
			log.info("\nVersion is UNKNOWN for JWBF (" + JWBF.getVersion() + ") : \n\t" + getGenerator() 
					+ "\n\tUsing settings for actual Wikipedia development version");
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
	public Map<String, String> getInterwikis(){
		return Collections.unmodifiableMap(interwiki);
	}
	/**
	 * @deprecated
	 * @return if is
	 */
	public boolean isWriteAPI() {
		return writeApi;
	}
	public void setWriteAPI(boolean writeApi) {
		this.writeApi = writeApi;
	}
}
