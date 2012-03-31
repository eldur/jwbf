package net.sourceforge.jwbf.core.bots;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.util.CacheHandler;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;

/**
 * Main abstraction interface for all kinds of wikibots.
 * 
 * @author Thomas Stock
 * 
 * 
 *         TODO change exception structure ... SocketException
 */
public interface WikiBot {

  /**
   * 
   * @param name
   *          of article in a mediawiki like "Main Page"
   * @return a content representation of requested article, never null
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   */
  Article readContent(String title);

  Article readContent(String title, int properties);

  SimpleArticle readData(final String name, final int properties);

  SimpleArticle readData(final String name);

  void writeContent(SimpleArticle sa);

  public void postDelete(String title);

  void login(String user, String passwd);

  Userinfo getUserinfo();

  String getWikiType();

  /**
   * 
   * @return if has
   */
  boolean hasCacheHandler();

  /**
   * 
   * @param ch
   *          a
   */
  void setCacheHandler(CacheHandler ch);
}
