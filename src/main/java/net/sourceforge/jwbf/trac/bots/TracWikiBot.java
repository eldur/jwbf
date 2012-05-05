package net.sourceforge.jwbf.trac.bots;

import java.net.MalformedURLException;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.HttpBot;
import net.sourceforge.jwbf.core.bots.WikiBot;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.trac.actions.GetRevision;

/**
 * /**
 * 
 * This class helps you to interact with each wiki as part of <a
 * href="http://trac.edgewall.org/" target="_blank">Trac</a>. This class offers
 * a set of methods which are defined in the package
 * net.sourceforge.jwbf.actions.trac.*
 * 
 * @author Thomas Stock
 * 
 * 
 */
public class TracWikiBot extends HttpBot implements WikiBot {

  /**
   * @param url
   *          wikihosturl like "http://trac.edgewall.org/wiki/"
   * @throws MalformedURLException
   *           if param url does not represent a well-formed url
   */
  public TracWikiBot(String url) throws MalformedURLException {
    super(url);
  }

  /**
   * 
   * @param name
   *          of article in a tracwiki like "TracWiki" , the main page is
   *          "WikiStart"
   * @return a content representation of requested article, never null
   * @throws ActionException
   *           on problems with http, cookies and io
   * @throws ProcessException
   *           on access problems
   * @see GetRevision
   */
  public synchronized Article readContent(final String name)
      throws ActionException, ProcessException {
    return readContent(name, 0);

  }

  public void login(String user, String passwd) {
    throw new IllegalStateException("Login is not supported");

  }

  public void writeContent(ContentAccessable sa) throws ActionException,
      ProcessException {
    throw new IllegalStateException("Writing is not supported");

  }

  public void postDelete(String title) throws ActionException, ProcessException {
    throw new IllegalStateException("Deleting is not supported");

  }

  public Article readContent(String label, int properties)
      throws ActionException, ProcessException {
    GetRevision ac = new GetRevision(label);
    performAction(ac);
    return new Article(this, ac.getArticle());
  }

  public SimpleArticle readData(String name, int properties) {
    throw new IllegalStateException();
  }

  public Userinfo getUserinfo() throws ActionException, ProcessException {
    throw new IllegalStateException();
  }

  public String getWikiType() {
    throw new IllegalStateException();
  }

  public SimpleArticle readData(String name) {
    throw new IllegalStateException();
  }

  public void writeContent(SimpleArticle sa) {
    throw new IllegalStateException();
  }

  public void delete(String title) {
    throw new IllegalStateException();

  }

}
