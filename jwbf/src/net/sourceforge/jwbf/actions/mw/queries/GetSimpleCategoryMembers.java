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
package net.sourceforge.jwbf.actions.mw.queries;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.jwbf.actions.mw.MultiAction;
import net.sourceforge.jwbf.actions.mw.util.ActionException;
import net.sourceforge.jwbf.actions.mw.util.ProcessException;
import net.sourceforge.jwbf.bots.util.JwbfException;
import net.sourceforge.jwbf.contentRep.mw.Version;

public class GetSimpleCategoryMembers extends GetCategoryMembers implements MultiAction<String>{

	
	/**
	 * Collection that will contain the result
	 * (titles of articles linking to the target) 
	 * after performing the action has finished.
	 */
	private Collection<String> titleCollection = new ArrayList<String>();
	
	private GetSimpleCategoryMembers(String nextPageInfo, String categoryName, String namespace, Version v) throws ActionException, ProcessException {
		super(nextPageInfo, categoryName, namespace, v);
	}

	public GetSimpleCategoryMembers(String categoryName, String namespace, Version v) throws ActionException, ProcessException {
		super(categoryName, namespace, v);
	}
	
	/**
	 * @return   the collected article names
	 */
	public Collection<String> getResults() {
		return titleCollection;	 
	}
	
	/**
	 * @return   necessary information for the next action
	 *           or null if no next api page exists
	 */
	public GetSimpleCategoryMembers getNextAction() {
		if (nextPageInfo == null) { 
			return null; 
		} else {
			try {
				return new GetSimpleCategoryMembers(nextPageInfo, categoryName, namespace, v);
			} catch (JwbfException e) {
				return null;
			}
		}
	}

	@Override
	protected void addCatItem(String title, int pageid, int ns) {
		titleCollection.add(title);

	}

}
