/*
 * Copyright 2007 Tobias Knerr.
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
 * 
 */
 
package net.sourceforge.jwbf.actions.mw;

import java.util.Collection;

import net.sourceforge.jwbf.actions.ContentProcessable;

/**
 * interface usable for those actions
 * that require more than one MW-request.
 * The type parameter R specifies the result type.
 *
 * @author Tobias Knerr
 * @since MediaWiki 1.9.0
 */
public interface MultiAction<R> extends ContentProcessable {

	/**
	 * @return   the results collecting during _this_ single action
	 */
	public Collection<R> getResults();
	
	/**
	 * @return   necessary information for the next action
	 *           or null if this was the last one
	 */
	public MultiAction getNextAction();
	
}
