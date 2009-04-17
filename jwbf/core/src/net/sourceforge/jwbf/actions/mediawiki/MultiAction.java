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
 
package net.sourceforge.jwbf.actions.mediawiki;

import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.ContentProcessable;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.bots.MediaWikiBot;

import org.apache.log4j.Logger;

/**
 * interface usable for those actions
 * that require more than one MW-request.
 * The type parameter R specifies the result type.
 *
 * @author Tobias Knerr
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 * @deprecated use {@link MWAction} instead
 */
public abstract class MultiAction<R> {
	private MediaWikiBot bot;
	private Logger log =  Logger.getLogger(getClass());
	private Collection<R> suCol;
	
	protected MultiAction(MediaWikiBot bot) {
		this.bot = bot;
	}
	/**
	 * @return   the results collecting during _this_ single action
	 */
	public Iterable<R> getResults() {
	
		
		return new MultiActionResultIterable<R>(this);
	}
	
	/**
	 * @return   necessary information for the next action
	 *           or null if this was the last one
	 */
	public abstract MultiAction<R> getNextAction();
	
	public abstract ContentProcessable getContentProcessable();
	
	
	/**
	 * Iterable-class which will store all results which are already known
	 * and perform the next action when more titles are needed
	 */
	@SuppressWarnings("hiding")
	class MultiActionResultIterable<R> implements Iterable<R> {

		private MultiAction<R> nextAction = null;

		

		/**
		 * constructor.
		 *
		 * @param initialAction
		 *            the
		 */
		public MultiActionResultIterable(MultiAction<R> initialAction) {
			this.nextAction = initialAction;
		}

	

		/**
		 * @return a
		 */
		public Iterator<R> iterator() {
			return new MultiActionResultIterator<R>(this);
		}

		/**
		 * matching Iterator, containing an index variable and a reference
		 * to a MultiActionResultIterable
		 */
		class MultiActionResultIterator<R> implements Iterator<R> {

			

			private MultiActionResultIterable<R> generatingIterable;

			/**
			 * constructor, relies on generatingIterable != null
			 *
			 * @param generatingIterable
			 *            a
			 */
			MultiActionResultIterator(
					MultiActionResultIterable<R> generatingIterable) {
				this.generatingIterable = generatingIterable;
			}

		

			/**
			 * if a new query is needed to request more; more results are
			 * requested.
			 *
			 * @return a element of iteration
			 */
			public R next() {
				// FIXME no return value
				return null;
//				while (index >= generatingIterable.knownResults.size()
//						&& generatingIterable.nextAction != null) {
//					generatingIterable.loadMoreResults();
//				}
//				return generatingIterable.knownResults.get(index++);
			}

			/**
			 * is not supported
			 */
			public void remove() {
				throw new UnsupportedOperationException();
			}



			public boolean hasNext() {
				return false;
			}

		}

	}
}
