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
package net.sourceforge.jwbf.contentRep.mw;



/**
 * 
 * @author Thomas Stock
 *
 */
public interface ContentAccessable {
	
	/**
	 * 
	 * @return the
	 */
	String getEditSummary();
	
	/**
	 * 
	 * @return the
	 */
	String getEditor();
	/**
	 * 
	 * @return true, if is
	 */
	boolean isMinorEdit(); 
	
	/**
	 * 
	 * @return titel
	 */
	String getLabel(); 
	
	/**
	 * 
	 * @return content wiki syntax of this article
	 */
	String getText();

}
