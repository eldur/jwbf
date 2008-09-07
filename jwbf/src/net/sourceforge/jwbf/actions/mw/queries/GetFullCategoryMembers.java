package net.sourceforge.jwbf.actions.mw.queries;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.util.JwbfException;
import net.sourceforge.jwbf.contentRep.mw.CategoryItem;
import net.sourceforge.jwbf.contentRep.mw.Version;


public class GetFullCategoryMembers extends GetCategoryMembers implements MultiAction<CategoryItem> {

	
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<CategoryItem> titleCollection = new ArrayList<CategoryItem>();
	
	public GetFullCategoryMembers(String articleName, String namespace, Version v) throws ActionException, ProcessException {
		super(articleName, namespace, v);

	}
	private GetFullCategoryMembers(String nextPageInfo, String categoryName, String namespace, Version v) throws ActionException, ProcessException{
		super(nextPageInfo, categoryName, namespace, v);
	}
	
	/**
	 * @return   the collected article names
	 */
	public Collection<CategoryItem> getResults() {
		return titleCollection;	 
	}
	
	/**
	 * @return   necessary information for the next action
	 *           or null if no next api page exists
	 */
	public GetFullCategoryMembers getNextAction() {
		if (nextPageInfo == null) { 
			return null; 
		} else {
			try {
				return new GetFullCategoryMembers(nextPageInfo, categoryName, namespace, v);
			} catch (JwbfException e) {
				return null;
			}
		}
	}
	@Override
	protected void addCatItem(String title, int pageid, int ns) {
		CategoryItem ci = new CategoryItem();
		ci.setTitle(title);
		ci.setPageid(pageid);
		ci.setNamespace(ns);
		titleCollection.add(ci);
		
	}

	
}
