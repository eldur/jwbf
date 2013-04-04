/**
 *
 */
package net.sourceforge.jwbf.inyoka.bots;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.inyoka.actions.GetRevision;

/**
 * 
 * This class helps you to interact with each wiki as part of <a href="http://ubuntuusers.de"
 * target="_blank">Inyoka</a>. This class offers a set of methods which are defined in the package
 * net.sourceforge.jwbf.actions.inyoka.*
 * 
 * @author Thomas Stock
 * 
 */
public class InyokaWikiBot extends HttpBot implements WikiBot {

  private static int DEFAULT = 0;

  /**
   * @param url
   *          wikihosturl like "http://wiki.ubuntuusers.de/Startseite?action=export&format=raw&"
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
   * 
   * @see GetRevision
   */
  public synchronized Article getArticle(final String name) {
    return getArticle(name, 0);

  }

  public void login(String user, String passwd) {
    throw new ActionException("Login is not supported");

  }

  public void writeContent(SimpleArticle sa) {
    throw new ActionException("Writing is not supported");

  }

  public void delete(String title) {
    throw new ActionException("Deleting is not supported");

  }

  public synchronized Article getArticle(String name, int properties) {
    return new Article(this, readData(name, properties));
  }

  public SimpleArticle readData(String name, int properties) {
    GetRevision ac = new GetRevision(name);
    performAction(ac);
    return ac.getArticle();
  }

  public Userinfo getUserinfo() {
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

  public SimpleArticle readData(String name) {
    return readData(name, DEFAULT);
  }

  public void setCacheHandler(Map<String, SimpleArticle> cache) {
    // TODO Auto-generated method stub

  }
}
