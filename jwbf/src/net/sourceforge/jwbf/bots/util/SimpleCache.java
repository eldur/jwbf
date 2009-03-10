package net.sourceforge.jwbf.bots.util;

import java.util.HashMap;

import net.sourceforge.jwbf.contentRep.SimpleArticle;

public class SimpleCache implements CacheHandler {

	private HashMap<String, SimpleArticle> store = new HashMap<String, SimpleArticle>();
	
	public boolean containsKey(String title) {
		return store.containsKey(title);
	}

	public SimpleArticle get(String titel) {
		return store.get(titel);
	}

	public void put(SimpleArticle sa) {
		store.put(sa.getLabel(), sa);
		
	}

}
