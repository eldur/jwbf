package net.sourceforge.jwbf.core.bots;

import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.bots.util.CacheHandler;
import net.sourceforge.jwbf.core.contentRep.Article;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
/**
 * Main abstraction interface for all kinds of wikibots.
 * @author Thomas Stock
 *
 */
public interface WikiBot {

	
	Article readContent(String title) throws ActionException, ProcessException ;
	Article readContent(String title, int properties) throws ActionException, ProcessException;
	SimpleArticle readData(final String name, final int properties) throws ActionException, ProcessException;
	SimpleArticle readData(final String name) throws ActionException, ProcessException;
	void writeContent(ContentAccessable sa) throws ActionException, ProcessException;
	public void postDelete(String title) throws ActionException, ProcessException; 
	
	
	void login(String user, String passwd) throws ActionException;
	Userinfo getUserinfo() throws ActionException, ProcessException;
	String getWikiType();
	
	boolean hasCacheHandler();
	void setCacheHandler(CacheHandler ch);
}
