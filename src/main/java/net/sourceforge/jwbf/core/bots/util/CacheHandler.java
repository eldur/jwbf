package net.sourceforge.jwbf.core.bots.util;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

/**
 * @deprecated use a map
 */
@Deprecated
public interface CacheHandler {

  void put(SimpleArticle sa);

  SimpleArticle get(String title);

  boolean containsKey(String title);
}
