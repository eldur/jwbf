/*
 * Copyright 2007 Philipp Kohl.
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
package net.sourceforge.jwbf.contentRep;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Philipp Kohl 
 *
 * Simple Test-Case to get Unit-Testing started
 */
public class SimpleArticleTest {

	
	private SimpleArticle article;
	
	/**
	 * setup. 
	 */
	@Before
	public void setUp() {

		article = new SimpleArticle();
	}
	/**
	 * Tests edit summary.
	 *
	 */
	@Test
	public void testEditSummary() {
		article.setEditSummary("test");
		assertEquals("test", article.getEditSummary());
	}
}
