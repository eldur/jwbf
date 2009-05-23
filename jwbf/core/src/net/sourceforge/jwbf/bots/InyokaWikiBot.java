/**
 * 
 */
package net.sourceforge.jwbf.bots;

import static net.sourceforge.jwbf.contentRep.SimpleArticle.COMMENT;
import static net.sourceforge.jwbf.contentRep.SimpleArticle.CONTENT;
import static net.sourceforge.jwbf.contentRep.SimpleArticle.USER;

import java.net.MalformedURLException;

import net.sourceforge.jwbf.actions.inyoka.GetRevision;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.ContentAccessable;
import net.sourceforge.jwbf.contentRep.SimpleArticle;
import net.sourceforge.jwbf.contentRep.Userinfo;
/**
 * 
 * This class helps you to interact with each wiki as part of
 * <a href="http://ubuntuusers.de" target="_blank">Inyoka</a>. This class offers
 * a set of methods which are defined in the package net.sourceforge.jwbf.actions.inyoka.*
 * 
 * @author Thomas Stock
 *
 */
public class InyokaWikiBot extends HttpBot implements WikiBot {

	/**
	 * @param url
	 *            wikihosturl like "http://wiki.ubuntuusers.de/Startseite?action=export&format=raw&"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public InyokaWikiBot(String url) throws MalformedURLException {
		super(url);
	}

	
	/**
	 *
	 * @param name
	 *            of article
	 * @return a content representation of requested article, never null
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 * @see GetRevision
	 */
	public synchronized Article readContent(final String name)
			throws ActionException, ProcessException {
		return readContent(name, CONTENT
				| COMMENT | USER );

	}
	
	public void login(String user, String passwd) throws ActionException {
		throw new ActionException("Login is not supported", getClass());
		
	}


	public void writeContent(ContentAccessable sa) throws ActionException,
			ProcessException {
		throw new ActionException("Writing is not supported", getClass());
		
	}


	public void postDelete(String title) throws ActionException,
			ProcessException {
		throw new ActionException("Deleting is not supported", getClass());
		
	}


	public synchronized Article readContent(String name, int properties)
			throws ActionException, ProcessException {
		GetRevision ac = new GetRevision(name);
		performAction(ac);
		return new Article(this, ac.getArticle()); 
	}


	public SimpleArticle readData(String name, int properties)
			throws ActionException, ProcessException {
		// TODO Auto-generated method stub
		return null;
	}


	public Userinfo getUserinfo() throws ActionException, ProcessException {

		return new Userinfo("unknown");
	}


	public String getWikiType() {
		// TODO Auto-generated method stub
		return null;
	}
}
