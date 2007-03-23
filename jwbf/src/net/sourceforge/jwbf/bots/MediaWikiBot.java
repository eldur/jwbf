/*
 * Copyright 2007 Thomas Stock.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors:
 * 
 */
package net.sourceforge.jwbf.bots;


import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.NamingException;

import net.sourceforge.jwbf.actions.http.ActionException;
import net.sourceforge.jwbf.actions.http.mw.GetCategoryElements;
import net.sourceforge.jwbf.actions.http.mw.GetEnvironmentVars;
import net.sourceforge.jwbf.actions.http.mw.GetPageContent;
import net.sourceforge.jwbf.actions.http.mw.PostDelete;
import net.sourceforge.jwbf.actions.http.mw.PostLogin;
import net.sourceforge.jwbf.actions.http.mw.PostModifyContent;
import net.sourceforge.jwbf.contentRep.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.EditContentAccessable;

/**
 * 
 * This class helps you to interact with each mediawiki.
 * 
 * How to use:
 * <pre>
 * MediaWikiBot b = new MediaWikiBot("http://yourwiki.org");
 * b.login("Username", "Password");
 * System.out.writeln(b.readContentOf("Main Page").getText());
 * </pre>
 * 
 * @author Thomas Stock
 *
 */
public class MediaWikiBot extends HttpBot {

	
	private boolean loggedIn = false;
	/**
	 * @param u wikihosturl like "http://www.mediawiki.org/wiki/index.php"
	 */
	public MediaWikiBot(final URL u) {
		super();
		setConnection(u);
	}
	
	/**
	 * Performs a Login.
	 * @param username the username
	 * @param passwd the password
	 * @throws ActionException on problems
	 */
	public final void login(final String username, final String passwd) 
		throws ActionException {			
			performAction(new PostLogin(username, passwd));
			loggedIn = true;

	}
	
	/**
	 * 
	 * @param name of article in a mediawiki like "Main Page"
	 * @return a content representation of requested article, never null
	 * @throws ActionException on problems or if conent null
	 */
	public final ContentAccessable readContentOf(final String name) throws ActionException {
		MwContentHelper a = null;
		
			a = new MwContentHelper(performAction(new GetPageContent(name)), name);
			
		return a;
	}
	/**
	 * 
	 * @param title of category in a mediawiki like "Category:Small Things"
	 * @return with all article names in the requestet category
	 * @throws ActionException on problems
	 */
	public final Iterator<String> readCategory(final String title) throws ActionException {
		Vector<String> av = new Vector<String>();
		
			
			
			GetCategoryElements cel = new GetCategoryElements(title, av);
			performAction(cel);
			try {
				while (cel.hasMore()) {
					cel = new GetCategoryElements(title, cel.next().toString(), av);
					performAction(cel);
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
			
			if (av.isEmpty()) {
				throw new ActionException("Category: \"" + title + "\" is empty");
			}
			
		return av.iterator();
	}
	/**
	 * 
	 * @param a write the article (if already exists) in the mediawiki
	 * @throws ActionException on problems
	 */
	public final void writeContent(final EditContentAccessable a) throws ActionException {
			
		if (!loggedIn) {
			throw new ActionException("Please login first");
		}
		Hashtable<String, String> tab = new Hashtable<String, String>(); 
		performAction(new GetEnvironmentVars(a.getLabel(), tab));
		performAction(new PostModifyContent(a, tab));
		
	}
	/**
	 * Writes an iteration of ContentAccessables to the mediawiki.
	 * @param cav a
	 * @throws ActionException on problems
	 */
	public final void writeMultContent(final Iterator<EditContentAccessable> cav) throws ActionException {
		while (cav.hasNext()) {
			writeContent(cav.next());
			
			
		}
	}
	/**
	 * 
	 * @param label like "Tamplate:FooBar" or "Main Page"
	 * @throws ActionException on problems
	 */
	public final void deleteArticle(final String label) throws ActionException {
		
		
			Hashtable<String, String> tab = new Hashtable<String, String>(); 
			 performAction(new GetEnvironmentVars(label, tab));
			 
			 performAction(new PostDelete(label, tab));
		
	}
	
	/**
	 * 
	 * @author Thomas Stock
	 *
	 */
	private class MwContentHelper implements ContentAccessable {

		private String text = "";
		private String label = "";
		/**
		 * 
		 * @param text of article
		 * @param label of article
		 */
		MwContentHelper(final String text, final String label) {
			this.text = text;
			this.label = label;
		}
		/**
		 *
		 * @see net.sourceforge.jwbf.contentRep.ContentAccessable#getLabel()
		 * @return the
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * @see net.sourceforge.jwbf.contentRep.ContentAccessable#getText()
		 * @return the
		 */
		public String getText() {
			return text;
		}
		
	}

}
