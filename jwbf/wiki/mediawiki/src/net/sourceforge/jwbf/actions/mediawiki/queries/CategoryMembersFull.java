package net.sourceforge.jwbf.actions.mediawiki.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;
import net.sourceforge.jwbf.contentRep.mediawiki.CategoryItem;

import org.apache.log4j.Logger;

/**
 * 
 * @author Thomas Stock
 * @supportedBy MediaWikiAPI 1.11, 1.12, 1.13, 1.14
 */
public class CategoryMembersFull extends CategoryMembers implements Iterable<CategoryItem>, Iterator<CategoryItem> {

	
	private Get msg;
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<CategoryItem> titleCollection = new ArrayList<CategoryItem>();
	private Iterator<CategoryItem> titleIterator;
	
	private Logger log = Logger.getLogger(getClass());
	
	/**
	 *
	 * @param category like "Buildings" or "Chemical elements" without prefix Category
	 * @return of category items with more details as simple labels
	 * @throws ActionException on any kind of http or version problems
	 * @throws ProcessException on inner problems like a version mismatch
	 */
	public CategoryMembersFull(MediaWikiBot bot, String categoryName , int... namespaces) throws ActionException, ProcessException {
		
		super(bot, categoryName, namespaces);
	

	}

	

	@Override
	protected void addCatItem(String title, int pageid, int ns) {
		CategoryItem ci = new CategoryItem();
		ci.setTitle(title);
		ci.setPageid(pageid);
		ci.setNamespace(ns);
		titleCollection.add(ci);
		
	}

	public HttpAction getNextMessage() {
		return msg;
	}
	public Iterator<CategoryItem> iterator() {
		return this;
//		try {
//			return (Iterator<CategoryItem>) this.clone();
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//			return null;
//		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			return new CategoryMembersFull(bot, categoryName, namespace);
		} catch (JwbfException e) {
			throw new CloneNotSupportedException(e.getLocalizedMessage());
		}
	}



	private void prepareCollection() {

		if (init || (!titleIterator.hasNext() && hasMoreResults)) {
			if(init) {
				msg = generateFirstRequest();
			} else {
				msg = generateContinueRequest(nextPageInfo);
			}
			init = false;
			try {

				bot.performAction(this);
				setHasMoreMessages(true);
				if (log.isDebugEnabled())
					log.debug("preparing success");
			} catch (ActionException e) {
				e.printStackTrace();
				setHasMoreMessages(false);
			} catch (ProcessException e) {
				e.printStackTrace();
				setHasMoreMessages(false);
			}

		}
	}
	
	@Override
	public String processAllReturningText(String s) throws ProcessException {
		titleCollection.clear();
		String buff = super.processAllReturningText(s);
		
		
		if (log.isDebugEnabled())
			log.debug(titleCollection);
		return buff;
	}

	public boolean hasNext() {
		prepareCollection();
		return titleIterator.hasNext(); 
	}

	public CategoryItem next() {
		prepareCollection();	
		return titleIterator.next();
	}

	public void remove() {
		titleIterator.remove();
		
	}
	
	@Override
	protected void finalizeParse() {
		titleIterator = titleCollection.iterator();
		
	}
}
