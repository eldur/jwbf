package net.sourceforge.jwbf.bots.util;

import net.sourceforge.jwbf.contentRep.SimpleArticle;

public interface CacheHandler {

	
	void put(SimpleArticle sa);
	SimpleArticle get(String titel);
	boolean containsKey(String title);
}
