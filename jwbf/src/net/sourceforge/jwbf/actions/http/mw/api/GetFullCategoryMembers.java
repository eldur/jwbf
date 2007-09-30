package net.sourceforge.jwbf.actions.http.mw.api;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.jwbf.contentRep.mw.CategoryItem;


public class GetFullCategoryMembers extends GetCategoryMembers implements MultiAction<CategoryItem> {

	
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<CategoryItem> titleCollection = new ArrayList<CategoryItem>();
	
	public GetFullCategoryMembers(String articleName) {
		super(articleName);

	}
	private GetFullCategoryMembers(String nextPageInfo, String categoryName){
		super(nextPageInfo, categoryName);
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
			return new GetFullCategoryMembers(nextPageInfo, categoryName);
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
