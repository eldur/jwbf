///*
// * Copyright 2007 Thomas Stock.
// * 
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// * 
// * http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// * 
// * Contributors:
// * 
// */
//package net.sourceforge.jwbf.actions.http.mw;
//
//import static org.junit.Assert.*;
//
//import java.util.Vector;
//
//import net.sourceforge.jwbf.FileLoader;
//
//import org.htmlparser.Node;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * 
// * @author Thomas Stock
// *
// */
//public class GetCategoryArticlesTest {
//
//	private GetCategoryArticles ga;
//	
//	/**
//	 * 
//	 * setup.
//	 */
//	@Before
//	public void setUp() {
//		ga = new GetCategoryArticles("", new Vector<String>());
//	}
//	/**
//	 * Test if category has more pages.
//	 *
//	 */
//	@Test
//	public final void testParseHasMore() {
//		Node text1 = getText(ga, FileLoader.MW1_10
//				+ "category_Weiblicher_Vorname_wp1_10_de.htm");
//		ga.parseHasMore(text1);
//		assertEquals(true, ga.hasMoreElements());
//		
//	}
//	/**
//	 * Test if next category element is: Evelyn.
//	 *
//	 */
//	@Test
//	public final void testGetNextPageId() {
//		Node text1 = getText(ga, FileLoader.MW1_10
//				+ "category_Weiblicher_Vorname_wp1_10_de.htm");
//		ga.parseHasMore(text1);
//		assertEquals("Evelyn", ga.nextElement());
//	}
//	/**
//	 * Test if no more pages are found.
//	 *
//	 */
//	@Test
//	public final void testGetNextPageId2No() {
//		Node text1 = getText(ga, FileLoader.MW1_10
//				+ "category_Pigenavne_wp1_10_dk.htm");
//		ga.parseHasMore(text1);
//		assertEquals("", ga.nextElement());
//	}
//	
//	/**
//	 * Test if no more pages are found.
//	 *
//	 */
//	@Test
//	public final void testParseHasMoreNo() {
//		Node text1 = getText(ga, FileLoader.MW1_10
//				+ "category_Pigenavne_wp1_10_dk.htm");
//		ga.parseHasMore(text1);
//		assertEquals(false, ga.hasMoreElements());
//	}
//	/**
//	 * 
//	 * @param gl a
//	 * @param file a
//	 * @return a
//	 */
//	private static Node getText(GetCategoryArticles gl, String file) {
//		return gl.getDivBodyContent(FileLoader.readFromFile(file));
//	}
//}
