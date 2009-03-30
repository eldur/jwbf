package net.sourceforge.jwbf.bots.util;

import java.util.HashMap;

import net.sourceforge.jwbf.contentRep.SimpleArticle;

import org.apache.log4j.Logger;

public class SimpleCache implements CacheHandler {

	private HashMap<String, SimpleArticle> store = new HashMap<String, SimpleArticle>();
	private HashMap<String, Long> timer = new HashMap<String, Long>();
	private Logger log = Logger.getLogger(SimpleCache.class);
	
	private final int timeout;
	
	
	public SimpleCache() {
		timeout = -1;
	}
	/**
	 * 
	 * @param milis timeout in milliseconds
	 */
	public SimpleCache(int milis) {
		timeout = milis;
	}

	public boolean containsKey(String title) {
		if (timeout > 0 && store.containsKey(title) && System.currentTimeMillis() > timer.get(title)) {
			store.remove(title);
		}
		return store.containsKey(title);
	}

	public SimpleArticle get(String title) {
		if (timeout > 0 && containsKey(title) && System.currentTimeMillis() > timer.get(title)) {
			store.remove(title);
		}
		return store.get(title);
	}

	public void put(SimpleArticle sa) {
		
		System.out.println("put data:"); // TODO RM
		System.out.println("\t" + sa.getLabel());
		System.out.println("\t" + sa.getText());
		if (timeout > 0) {
			timer.put(sa.getLabel(), System.currentTimeMillis() + timeout);
		}
		store.put(sa.getLabel(), sa);
		
	}
	
	private void maintainData() {
		
	}

}
