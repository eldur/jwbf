/**
 *
 */
package net.sourceforge.jwbf.inyoka.bots;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.inyoka.actions.GetRevision;

/**
 * 
 * This class helps you to interact with each wiki as part of <a
 * href="http://ubuntuusers.de" target="_blank">Inyoka</a>. This class offers a
 * set of methods which are defined in the package
 * net.sourceforge.jwbf.actions.inyoka.*
 * 
 * @author Thomas Stock
 * 
 */
public class InyokaWikiBot extends HttpBot implements WikiBot {

  private static int DEFAULT = 0;

  /**
   * @param url
   *          wikihosturl like
   *          "http://wiki.ubuntuusers.de/Startseite?action=export&format=raw&"
   * @throws MalformedURLException
   *           if param url does not represent a well-formed url
   */
  public InyokaWikiBot(String url) throws MalformedURLException {
    super(url);
  }

  /**
   * 
   * @param name
   *          of article
   * @return a content representation of requested article, never null
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   * @see GetRevision
   */
  public synchronized Article getArticle(final String name)
      throws ActionException, ProcessException {
    return getArticle(name, 0);

  }

  public void login(String user, String passwd) throws ActionException {
    throw new ActionException("Login is not supported");

  }

  public void writeContent(SimpleArticle sa) throws ActionException,
      ProcessException {
    throw new ActionException("Writing is not supported");

  }

  public void delete(String title) throws ActionException, ProcessException {
    throw new ActionException("Deleting is not supported");

  }

  public synchronized Article getArticle(String name, int properties)
      throws ActionException, ProcessException {
    return new Article(this, readData(name, properties));
  }

  public SimpleArticle readData(String name, int properties)
      throws ActionException, ProcessException {
    GetRevision ac = new GetRevision(name);
    performAction(ac);
    return ac.getArticle();
  }

  public Userinfo getUserinfo() throws ActionException, ProcessException {
    // TODO incomplete
    return new Userinfo() {

      public String getUsername() {
        return "unknown";
      }

      public Set<String> getRights() {
        // TODO Auto-generated method stub
        return null;
      }

      public Set<String> getGroups() {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  public String getWikiType() {
    // TODO Auto-generated method stub
    return null;
  }

  public boolean hasCacheHandler() {
    // TODO Auto-generated method stub
    return false;
  }

  public SimpleArticle readData(String name) throws ActionException,
      ProcessException {
    return readData(name, DEFAULT);
  }

  public void setCacheHandler(Map<String, SimpleArticle> cache) {
    // TODO Auto-generated method stub

  }
}
