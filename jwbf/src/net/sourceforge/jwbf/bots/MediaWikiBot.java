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
 * Tobias Knerr
 */
 
package net.sourceforge.jwbf.bots;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.http.ActionException;
import net.sourceforge.jwbf.actions.http.mw.GetEnvironmentVars;
import net.sourceforge.jwbf.actions.http.mw.MWAction;
import net.sourceforge.jwbf.actions.http.mw.PostDelete;
import net.sourceforge.jwbf.actions.http.mw.PostLoginOld;
import net.sourceforge.jwbf.actions.http.mw.PostModifyContent;
import net.sourceforge.jwbf.actions.http.mw.api.GetAllPageTitles;
import net.sourceforge.jwbf.actions.http.mw.api.GetBacklinkTitles;
import net.sourceforge.jwbf.actions.http.mw.api.GetImagelinkTitles;
import net.sourceforge.jwbf.actions.http.mw.api.GetRevision;
import net.sourceforge.jwbf.actions.http.mw.api.GetTemplateUserTitles;
import net.sourceforge.jwbf.actions.http.mw.api.MultiAction;
import net.sourceforge.jwbf.bots.util.LoginData;

import net.sourceforge.jwbf.contentRep.mw.EditContentAccessable;

/*
 * possible tag values: @supportedBy
 * ------------------------------------------
 * MediaWiki 1.9.x
 * MediaWiki 1.9.x API
 * MediaWiki 1.10.x
 * MediaWiki 1.10.x API
 * 
 * ( current Wikipedia version )
 * MediaWiki 1.11.alpha
 * MediaWiki 1.11.alpha API
 * ------------------------------------------
 */

/**
 * 
 * This class helps you to interact with each mediawiki.
 * 
 * How to use:
 * 
 * <pre>
 * MediaWikiBot b = new MediaWikiBot(&quot;http://yourwiki.org&quot;);
 * b.login(&quot;Username&quot;, &quot;Password&quot;);
 * System.out.writeln(b.readContentOf(&quot;Main Page&quot;).getText());
 * </pre>
 * 
 * @author Thomas Stock
 * @author Tobias Knerr 
 * 
 */
public class MediaWikiBot extends HttpBot {
	
	public static final int ARTICLE = 1 << 1;
	public static final int MEDIA = 1 << 2;
	public static final int SUBCATEGORY = 1 << 3;
	
	public static final String CHARSET = "utf-8";
	
	private LoginData login;
	private boolean loggedIn = false;
	

	/**
	 * @param u
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiBot(final URL u) {
		super();
		setConnection(u);

	}
	
	/**
	 * @param url
	 *            wikihosturl like "http://www.mediawiki.org/wiki/"
	 */
	public MediaWikiBot(final String url) {
		super();
		setConnection(url);

	}

	

	
	/**
	 * Performs a Login.
	 * 
	 * @param username
	 *            the username
	 * @param passwd
	 *            the password
	 * @throws ActionException
	 *             on problems
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public final void login(final String username, final String passwd)
			throws ActionException {
		// code for 1.9.x API
//		PostLogin pl = new PostLogin(username, passwd);
//		performAction(pl);
//		login = pl.getLoginData();
		performAction(new PostLoginOld(username, passwd));
		loggedIn = true;
	}

	/**
	 * 
	 * @param name
	 *            of article in a mediawiki like "Main Page"
	 * @return a content representation of requested article, never null
	 * @throws ActionException
	 *             on problems or if conent null
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public final EditContentAccessable readContent(final String name)
			throws ActionException {
		EditContentAccessable a = null;
		GetRevision ac = new GetRevision(name, GetRevision.CONTENT | GetRevision.COMMENT | GetRevision.USER);
		
		performAction(ac);
		a = ac.getArticle();

		return a;
	}

//	/**
//	 * 
//	 * @param title
//	 *            of category in a mediawiki like "Category:Small Things"
//	 * @return with all article names in the requestet category
//	 * @throws ActionException
//	 *             on problems
//	 */
//	public final Collection<String> readCategory(final String title)
//			throws ActionException {
//
//		return readCategory(title, ARTICLE);
//	}


	/**
	 * helper method generating a namespace string as required by the MW-api.
	 * @param namespaces namespace as 
	 * @return with numbers seperated by |
	 */
	private String generateNamespaceString(int ... namespaces) {
	
		String namespaceString = null;
		
		if (namespaces != null && namespaces.length != 0) {
			
			namespaceString = new String();
			
			for (int nsNumber : namespaces) {
				
				namespaceString += nsNumber + "|";
				
			}
			
			//remove last '|'
			if (namespaceString.endsWith("|")) {
				namespaceString = namespaceString.substring(0, namespaceString.length() - 1);
			}			

		}
		
		return namespaceString;
		
	}
	
	/**
	 * generates an iterable with the results from a series of MultiAction
	 * when given the first of the actions.
	 * The result type can vary to match the result type of the MultiActions.
	 *
	 *
	 * @param initialAction   first action to perform, provides a next action.
	 * @param <R> sd
	 * @return   iterable providing access to the result values from the
	 *           responses to the initial and subsequent actions.
	 *           Attention: when the values from the subsequent actions 
	 *           are accessed for the first time,
	 *           the connection to the MediaWiki must still exist,
	 *           /*++ unless ...
	 *
	 * @throws ActionException   
	 *           general exception when problems concerning the action occur
	 * @supportedBy MediaWiki 1.9.x API
	 */
	@SuppressWarnings("unchecked")
	private <R> Iterable<R> performMultiAction(MultiAction<R> initialAction)
		throws ActionException {
		
		//Iterable-class which will store all results which are already known
		//and perform the next action when more titles are needed 
		class MultiActionResultIterable<R> implements Iterable<R> {
		
			//matching Iterator, containing an index variable
			//and a reference to a MultiActionResultIterable
			class MultiActionResultIterator<R> implements Iterator<R> {
			
				private int index = 0;
	
				private MultiActionResultIterable<R> generatingIterable;
				
				public boolean hasNext() { 
					while (index >= generatingIterable.knownResults.size() 
									&& generatingIterable.nextAction != null) {
						generatingIterable.loadMoreResults();
					}
					return index < generatingIterable.knownResults.size();						
				}
					
				public R next() {
					while (index >= generatingIterable.knownResults.size()
									&& generatingIterable.nextAction != null) {
						generatingIterable.loadMoreResults();
					}
					return generatingIterable.knownResults.get(index++);					
				}
				
				public void remove() { throw new UnsupportedOperationException(); }
				
				/** constructor, relies on generatingIterable != null */
				MultiActionResultIterator(
					MultiActionResultIterable<R> generatingIterable) {
					this.generatingIterable = generatingIterable;
				}																
				
			}   			
					
			private MultiAction<R> nextAction = null;
					
			private ArrayList<R> knownResults = new ArrayList<R>();
						

			private void loadMoreResults() {
				
				if (nextAction != null) {
					
					try {
					
						performAction((MWAction) nextAction); /*++ remove that cast! ++*/
						knownResults.addAll(nextAction.getResults());		
					
						nextAction = nextAction.getNextAction();
						
					} catch (ActionException ae) { nextAction = null; }
					
				}
				
			}
			
			public Iterator<R> iterator() {
				return new MultiActionResultIterator<R>(this);
			}
			
			public MultiActionResultIterable(MultiAction<R> initialAction) {
				this.nextAction = initialAction;
			}
			
		}
		
		return new MultiActionResultIterable(initialAction);

	}


	/*++ TODO: loadAll-parameter ++*/
	
	
	/**
	 * get the titles of all pages meeting certain criteria;
	 * USE WITH CAUTION - especially in big wikis!
	 *
	 * @param from          page title to start from, may be null
	 * @param prefix        restricts search to titles that begin with this value,
	 *                      may be null
	 * @param redirects     include redirects in the list
	 * @param nonredirects  include nonredirects in the list
	 * @param namespaces    numbers of the namespaces (specified using varargs)
	 *                      that will be included in the search
   *                      (will be ignored if redirects is false!)
	 * 
	 * @return   iterable providing access to the names of all articles
	 *           which embed the template specified by the template-parameter.
	 *           Attention: to get more article titles,
	 *           the connection to the MediaWiki must still exist.
	 *
	 * @throws ActionException   general exception when problems occur
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
		boolean redirects, boolean nonredirects, int... namespaces)
		throws ActionException {
				
		GetAllPageTitles a = new GetAllPageTitles(from, prefix,
			redirects, nonredirects, generateNamespaceString(namespaces));			

		return performMultiAction(a);
		
	}
	
	/**
	 * get the titles of all pages meeting certain criteria;
	 * USE WITH CAUTION - especially in big wikis!
	 *
	 * @param namespaces    numbers of the namespaces (specified using varargs)
	 *                      that will be included in the search
   *                      (will be ignored if redirects is false!)
	 * 
	 * @return   iterable providing access to the names of all articles
	 *           which embed the template specified by the template-parameter.
	 *           Attention: to get more article titles,
	 *           the connection to the MediaWiki must still exist.
	 *
	 * @throws ActionException   general exception when problems occur
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getAllPageTitles(int... namespaces)
		throws ActionException {
				
		GetAllPageTitles a = new GetAllPageTitles(null, null,
			false, true, generateNamespaceString(namespaces));			

		return performMultiAction(a);
		
	}	
	
	/**
	 * variation of the getAllPageTitles-method
	 * which does not set a namespace restriction.
	 * @param from 
	 * @param prefix
	 * @param redirects
	 * @param nonredirects
	 * @supportedBy MediaWiki 1.9.x, 1.10.x API
	 */
	public Iterable<String> getAllPageTitles(String from, String prefix,
		boolean redirects, boolean nonredirects) throws ActionException {
				
		return getAllPageTitles(from, prefix, redirects, nonredirects, null);	
		
	}

	
	/**
	 * get the titles of all pages which contain a link to the given article.
	 *
	 * @param article   title of an article
	 *
	 * @param namespaces   numbers of the namespaces (specified using varargs)
	 *                     that will be included in the search
	 * 
	 * @return   iterable providing access to the names of all articles
	 *           which link to the article specified by the article-parameter.
	 *           Attention: to get more article titles,
	 *           the connection to the MediaWiki must still exist.
	 *
	 * @throws ActionException   general exception when problems occur
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getBacklinkTitles(
		String article, int... namespaces) throws ActionException {
					
		GetBacklinkTitles a = new GetBacklinkTitles(
			article, generateNamespaceString(namespaces));			

		return performMultiAction(a);
		
	}
	
	/**
	 * variation of the getBacklinkTitles-method
	 * which does not set a namespace restriction.
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getBacklinkTitles(
		String article) throws ActionException {
				
		return getBacklinkTitles(article, null);	
		
	}
	

	/**
	 * get the titles of all pages which contain a link to the given image.
	 *
	 * @param image   title of an image
	 *
	 * @param namespaces   numbers of the namespaces (specified using varargs)
	 *                     that will be included in the search
	 * 
	 * @return   iterable providing access to the names of all articles
	 *           which link to the image specified by the image-parameter.
	 *           Attention: to get more article titles,
	 *           the connection to the MediaWiki must still exist.
	 *
	 * @throws ActionException   general exception when problems occur
	 * @since MediaWiki 1.9.0 API
	 */
	public Iterable<String> getImagelinkTitles(
		String image, int... namespaces) throws ActionException {
				
		GetImagelinkTitles a = new GetImagelinkTitles(
			image, generateNamespaceString(namespaces));			

		return performMultiAction(a);
		
	}	
	
	/**
	 * variation of the getImagelinkTitles-method
	 * which does not set a namespace restriction.
	 * @see #getImagelinkTitles(String, int[])
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getImagelinkTitles(
		String image) throws ActionException {
				
		return getImagelinkTitles(image, null);	
		
	}	

	
	/**
	 * get the titles of all pages which embed the given template.
	 *
	 * @param template   title of a template
	 *
	 * @param namespaces   numbers of the namespaces (specified using varargs)
	 *                     that will be included in the search
	 * 
	 * @return   iterable providing access to the names of all articles
	 *           which embed the template specified by the template-parameter.
	 *           Attention: to get more article titles,
	 *           the connection to the MediaWiki must still exist.
	 *
	 * @throws ActionException   general exception when problems occur
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getTemplateUserTitles(
		String template, int... namespaces) throws ActionException {
				
		GetTemplateUserTitles a = new GetTemplateUserTitles(
			template, generateNamespaceString(namespaces));			

		return performMultiAction(a);
		
	}	
	
	/**
	 * variation of the getTemplateUserTitles-method.
	 * which does not set a namespace restriction
	 * @supportedBy MediaWiki 1.9.x API
	 */
	public Iterable<String> getTemplateUserTitles(
		String template) throws ActionException {
				
		return getTemplateUserTitles(template, null);	
		
	}		

	/**
	 * 
	 * @return true if
	 */
	public boolean isLoggedIn() {
		return loggedIn;
//		// code for api 
//		if(login != null) {
//			return true;
//		}
//		return false;
	}

	/**
	 * 
	 * @param a
	 *            write the article (if already exists) in the mediawiki
	 * @throws ActionException
	 *             on problems
	 * @supportedBy MediaWiki 1.9.x
	 */
	public final void writeContent(final EditContentAccessable a)
			throws ActionException {

		if (!isLoggedIn()) {
			throw new ActionException("Please login first");
		}
		Hashtable<String, String> tab = new Hashtable<String, String>();
		performAction(new GetEnvironmentVars(a.getLabel(), tab, login));
		performAction(new PostModifyContent(a, tab, login));

	}

	/**
	 * Writes an iteration of ContentAccessables to the mediawiki.
	 * 
	 * @param cav
	 *            a
	 * @throws ActionException
	 *             on problems
	 * @supportedBy MediaWiki 1.9.x
	 */
	public final void writeMultContent(final Iterator<EditContentAccessable> cav)
			throws ActionException {
		while (cav.hasNext()) {
			writeContent(cav.next());

		}
	}

	/**
	 * 
	 * @param label
	 *            like "Tamplate:FooBar" or "Main Page"
	 * @throws ActionException
	 *             on problems
	 * @supportedBy MediaWiki 1.8.x, 1.9.x
	 * @deprecated
	 */
	public final void deleteArticle(final String label) throws ActionException {

		Hashtable<String, String> tab = new Hashtable<String, String>();
		performAction(new GetEnvironmentVars(label, tab, login));

		performAction(new PostDelete(label, tab));

	}
	

}
