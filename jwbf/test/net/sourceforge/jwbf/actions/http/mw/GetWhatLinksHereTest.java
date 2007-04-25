package net.sourceforge.jwbf.actions.http.mw;

import static org.junit.Assert.*;

import java.util.Vector;

import net.sourceforge.jwbf.FileLoader;

import org.htmlparser.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the WhatLinksHere.
 * 
 * @author Thomas Stock
 * 
 */
public class GetWhatLinksHereTest {

	private GetWhatlinkshereElements gl;

	/**
	 * 
	 * setUp the action.
	 */
	@Before
	public void setUp() {
		gl = new GetWhatlinkshereElements("", new Vector<String>());

	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find the next page link. German
	 */
	@Test
	public final void testMW110ParseHasMoreTrue1() {
		Node text1 = getHTML(gl, FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_de.htm");
		gl.parseHasMore(text1);
		boolean hasMore = gl.hasMoreElements();
		assertEquals(true, hasMore);
	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find the next page link id. German
	 */
	@Test
	public final void testMW110GetNextPageId1() {
		Node text1 = getHTML(gl, FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_de.htm");
		gl.parseHasMore(text1);
		String hasMore = gl.nextElement() + "";
		assertEquals("864323", hasMore);
	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find the next page link. Spanish
	 */
	@Test
	public final void testMW110ParseHasMoreTrue2() {
		Node text1 = getHTML(gl, FileLoader.MW1_10
				+ "whatLinks2IngenieriaDeSoftware_wp1_10_es.htm");
		gl.parseHasMore(text1);
		boolean hasMore = gl.hasMoreElements();
		assertEquals(true, hasMore);
	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find the next page link id. Spanish
	 */
	@Test
	public final void testMW110GetNextPageId2() {
		Node text1 = getHTML(gl, FileLoader.MW1_10
				+ "whatLinks2IngenieriaDeSoftware_wp1_10_es.htm");
		gl.parseHasMore(text1);
		String hasMore = gl.nextElement() + "";
		assertEquals("350470", hasMore);
	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find NOT the next page link. French
	 */
	@Test
	public final void testMW110ParseHasMoreFalse1() {
		Node text1 = getBodyContent(gl, FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_fr.htm");
		gl.parseHasMore(text1);
		boolean hasMore = gl.hasMoreElements();
		assertEquals(false, hasMore);
	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find NOT the next page link id.
	 * French
	 */
	@Test
	public final void testMW110ParseHasMoreFalse2() {
		Node text1 = getBodyContent(gl, FileLoader.MW1_10
				+ "whatLinks2Ohjelmistotuottanto_wp1_10_fi.htm");
		gl.parseHasMore(text1);
		boolean hasMore = gl.hasMoreElements();
		assertEquals(false, hasMore);
	}

	/**
	 * Tests MediaWiki 1.10 compatibility to find the number of the next page
	 * links. German
	 */
	@Test
	public final void testMW110NumberOfWhatLinks1() {
		Node text1 = getBodyContent(gl, FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_de.htm");
		int has = gl.getArticles(text1).size();
		assertEquals(61, has);

	}
	/**
	 * Tests MediaWiki 1.10 compatibility of charcter encoding.
	 * German
	 */
	@Test
	public final void testMW110EncodingOfElements1() {
		Node text1 = getBodyContent(gl, FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_de.htm");
		boolean bool = gl.getArticles(text1).contains("Reliabilität");
		assertEquals(true, bool);

	}
	/**
	 * Tests MediaWiki 1.10 compatibility of charcter encoding.
	 * German
	 */
	@Test
	public final void testMW110EncodingOfElements2() {
		Node text1 = getBodyContent(gl, FileLoader.MW1_10
				+ "whatLinks2Test_wp1_10_de.htm");
		gl.parseHasMore(text1);
		boolean bool = gl.getArticles(text1).contains("Dyskalkulie");
		assertEquals(true, bool);

	}

	/**
	 * Tests MediaWiki 1.9.3 compatibility with limit = 2 .
	 * 
	 */
	@Test
	public final void testMW193Whatlinks() {
		Node text1 = getHTML(gl, FileLoader.MW1_9_3 + "whatLinks2A_ws_de.htm");
		gl.parseHasMore(text1);
		String str = gl.nextElement() + "";
		assertEquals("6", str);

	}

	/**
	 * Tests MediaWiki 1.9.3 compatibility with default limit.
	 * 
	 */
	@Test
	public final void testMW193WhatlinksNoLimit() {
		Node text1 = getHTML(gl, FileLoader.MW1_9_3
				+ "whatLinks2Amore_ws_de.htm");
		gl.parseHasMore(text1);
		String str = gl.nextElement() + "";
		assertEquals("6", str);

	}

	/**
	 * 
	 * @param gl a
	 * @param file a
	 * @return a
	 */
	private static Node getBodyContent(GetWhatlinkshereElements gl, String file) {
		return gl.getDivBodyContent(gl.encode(FileLoader.readFromFile(file)));
	}
	/**
	 * 
	 * @param gl a
	 * @param file a
	 * @return a
	 */
	private static Node getHTML(GetWhatlinkshereElements gl, String file) {
		return gl.getHtmlBody(gl.encode(FileLoader.readFromFile(file)));
	}

}
