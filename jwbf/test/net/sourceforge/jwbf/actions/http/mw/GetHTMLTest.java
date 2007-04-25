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
package net.sourceforge.jwbf.actions.http.mw;

import static org.junit.Assert.*;

import net.sourceforge.jwbf.FileLoader;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Thomas Stock
 * 
 */
public class GetHTMLTest {

	private GetHTML gh;

	private String text1 = "";

	/**
	 * 
	 * @throws Exception
	 *             on problems
	 */
	@Before
	public void setUp() throws Exception {
		gh = new GetHTML();

		text1 = FileLoader.readFromFile(FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_de.htm");
	}

	/**
	 * Compare the textlength of main content; not a very good test.
	 */
	@Test
	public void testGetMainContent() {

		assertEquals(10214, gh.getDivBodyContent(text1).toHtml().length());
	}

}
