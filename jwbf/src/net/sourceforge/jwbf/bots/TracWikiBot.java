/**
 * 
 */
package net.sourceforge.jwbf.bots;


import java.net.MalformedURLException;

import net.sourceforge.jwbf.actions.trac.GetRevision;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.Article;

/**
/**
 * 
 * This class helps you to interact with each wiki as part of
 * <a href="http://trac.edgewall.org/" target="_blank">Trac</a>. This class offers
 * a set of methods which are defined in the package net.sourceforge.jwbf.actions.trac.*
 * 
 * @author Thomas Stock
 *
 *
 */
public class TracWikiBot extends HttpBot {

	/**
	 * @param url
	 *            wikihosturl like "http://trac.edgewall.org/wiki/"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public TracWikiBot(String url) throws MalformedURLException {
		super();
		setConnection(url);
	}

	
	/**
	 *
	 * @param name
	 *            of article in a tracwiki like "TracWiki"
	 *            , the main page is "WikiStart"
	 * @return a content representation of requested article, never null
	 * @throws ActionException
	 *             on problems with http, cookies and io
	 * @throws ProcessException on access problems
	 * @see GetRevision
	 */
	public synchronized Article readContent(final String name)
			throws ActionException, ProcessException {
		GetRevision ac = new GetRevision(name);
		performAction(ac);
		return new Article(ac.getArticle(), null); // FIXME rm null

	}

}
