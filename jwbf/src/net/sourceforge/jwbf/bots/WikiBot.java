package net.sourceforge.jwbf.bots;

import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.contentRep.Article;
import net.sourceforge.jwbf.contentRep.ContentAccessable;

public interface WikiBot {

	
	Article readContent(String label) throws ActionException, ProcessException ;
	void writeContent(ContentAccessable sa) throws ActionException, ProcessException;
	
	
	
	void login(String user, String passwd) throws ActionException;
	
}
