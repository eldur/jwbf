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

	private Map<Integer, String> namespaces = new HashMap<Integer, String>();
	private Map<String, String> interwiki = new HashMap<String, String>();
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
		if (getGenerator().contains("alpha")) {
			return Version.DEVELOPMENT;
		}
		
		Version [] versions = Version.values();
		for (int i = 0; i < versions.length; i++) {
			if (getGenerator().contains(versions[i].getNumber())) {
				return versions[i];
			}
			
		}
		
			log.info("\nVersion is UNKNOWN for JWBF (" + JWBF.getVersion() + ") : \n\t" + getGenerator() 
					+ "\n\tUsing settings for actual Wikipedia development version");
			return Version.UNKNOWN;
		
	
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
		namespaces.put(id, name);
		
	}

	public Map<Integer, String> getNamespaces() {
		return Collections.unmodifiableMap(namespaces);
	}

	public void addInterwiki(String prefix, String name) {
		interwiki.put(prefix, name);
	}
	public Map<String, String> getInterwikis() {
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
