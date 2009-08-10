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
package net.sourceforge.jwbf.mediawiki.contentRep;

import net.sourceforge.jwbf.mediawiki.actions.queries.CategoryMembersFull;

/**
 * This class helps to get detail information about category items 
 * and was returned by {@link CategoryMembersFull}.
 * @author Thomas Stock
 *
 */
public class CategoryItem {
	
	private String title = "";
	private int namespace;
	private int pageid;
	
	/**
	 * @return representation of the category member
	 */
	public String toString() {
		return title;
	}

	/**
	 * 
	 * @return the
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title the
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return the
	 */
	public int getNamespace() {
		return namespace;
	}

	/**
	 * 
	 * @param namespace the
	 */
	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}

	/**
	 * 
	 * @return the
	 */
	public int getPageid() {
		return pageid;
	}


	public void setPageid(int pageid) {
		this.pageid = pageid;
	}

}
