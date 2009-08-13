package net.sourceforge.jwbf.trac.bots;
///**
// * 
// */
//package net.sourceforge.jwbf.bots;
//
//
//import static net.sourceforge.jwbf.contentRep.SimpleArticle.COMMENT;
//import static net.sourceforge.jwbf.contentRep.SimpleArticle.CONTENT;
//import static net.sourceforge.jwbf.contentRep.SimpleArticle.USER;
//
//import java.net.MalformedURLException;
//
//import net.sourceforge.jwbf.actions.trac.GetRevision;
//import net.sourceforge.jwbf.actions.util.ActionException;
//import net.sourceforge.jwbf.actions.util.ProcessException;
//import net.sourceforge.jwbf.contentRep.Article;
//import net.sourceforge.jwbf.contentRep.ContentAccessable;
//import net.sourceforge.jwbf.contentRep.SimpleArticle;
//import net.sourceforge.jwbf.contentRep.Userinfo;
//
///**
///**
// * 
// * This class helps you to interact with each wiki as part of
// * <a href="http://trac.edgewall.org/" target="_blank">Trac</a>. This class offers
// * a set of methods which are defined in the package net.sourceforge.jwbf.actions.trac.*
// * 
// * @author Thomas Stock
// *
// *
// */
//public class TracWikiBot extends HttpBot implements WikiBot {
//
//	/**
//	 * @param url
//	 *            wikihosturl like "http://trac.edgewall.org/wiki/"
//	 * @throws MalformedURLException
//	 *            if param url does not represent a well-formed url
//	 */
//	public TracWikiBot(String url) throws MalformedURLException {
//		super(url);
//	}
//
//	
//	/**
//	 *
//	 * @param name
//	 *            of article in a tracwiki like "TracWiki"
//	 *            , the main page is "WikiStart"
//	 * @return a content representation of requested article, never null
//	 * @throws ActionException
//	 *             on problems with http, cookies and io
//	 * @throws ProcessException on access problems
//	 * @see GetRevision
//	 */
//	public synchronized Article readContent(final String name)
//			throws ActionException, ProcessException {
//		return readContent(name, CONTENT
//				| COMMENT | USER );
//		
//
//	}
//
//
//	public void login(String user, String passwd) throws ActionException {
//		throw new ActionException("Login is not supported", getClass());
//		
//	}
//
//
//	public void writeContent(ContentAccessable sa) throws ActionException,
//			ProcessException {
//		throw new ActionException("Writing is not supported", getClass());
//		
//	}
//
//
//	public void postDelete(String title) throws ActionException,
//			ProcessException {
//		throw new ActionException("Deleting is not supported", getClass());
//		
//	}
//
//
//	public Article readContent(String label, int properties)
//			throws ActionException, ProcessException {
//		GetRevision ac = new GetRevision(label);
//		performAction(ac);
//		return new Article(this, ac.getArticle());
//	}
//
//
//	public SimpleArticle readData(String name, int properties)
//			throws ActionException, ProcessException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	public Userinfo getUserinfo() throws ActionException, ProcessException {
//		return new Userinfo("unknown");
//	}
//
//
//	public String getWikiType() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
