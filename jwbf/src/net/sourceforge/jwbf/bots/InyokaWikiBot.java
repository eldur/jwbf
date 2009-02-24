/**
 * 
 */
package net.sourceforge.jwbf.bots;

import java.net.MalformedURLException;

import net.sourceforge.jwbf.actions.inyoka.GetRevision;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.SimpleArticle;

/**
 * 
 * This class helps you to interact with each wiki as part of
 * <a href="http://ubuntuusers.de" target="_blank">Inyoka</a>. This class offers
 * a set of methods which are defined in the package net.sourceforge.jwbf.actions.inyoka.*
 * 
 * @author Thomas Stock
 *
 */
public class InyokaWikiBot extends HttpBot {

	/**
	 * @param url
	 *            wikihosturl like "http://wiki.ubuntuusers.de/Startseite?action=export&format=raw&"
	 * @throws MalformedURLException
	 *            if param url does not represent a well-formed url
	 */
	public InyokaWikiBot(String url) throws MalformedURLException {
		super();
		setConnection(url);
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
	public synchronized SimpleArticle readContent(final String name)
			throws ActionException, ProcessException {
		GetRevision ac = new GetRevision(name);
		performAction(ac);
		return ac.getArticle(); 

	}
}
