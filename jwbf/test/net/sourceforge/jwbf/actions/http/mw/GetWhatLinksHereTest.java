package net.sourceforge.jwbf.actions.http.mw;

import static org.junit.Assert.*;

import java.util.Vector;

import net.sourceforge.jwbf.TestFileLoader;

import org.htmlparser.Node;
import org.junit.Before;
import org.junit.Test;


public class GetWhatLinksHereTest {

	private GetWhatlinkshereElements gl;

	@Before
	public void setUp() throws Exception {
		gl = new GetWhatlinkshereElements("", new Vector<String>());

	}

	@Test
	public final void testParseHasMoreTrue1() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Test_wp1_10_de.htm");
		boolean hasMore = gl.parseHasMore(text1);
		assertEquals(true, hasMore);
	}
	@Test
	public final void testGetNextPageId1() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Test_wp1_10_de.htm");
		int hasMore = gl.getNextPageId(text1);
		assertEquals(864323, hasMore);
	}

	@Test
	public final void testParseHasMoreTrue2() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2IngenieriaDeSoftware_wp1_10_es.htm");
		boolean hasMore = gl.parseHasMore(text1);
		assertEquals(true, hasMore);
	}
	
	@Test
	public final void testGetNextPageId2() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2IngenieriaDeSoftware_wp1_10_es.htm");
		int hasMore = gl.getNextPageId(text1);
		assertEquals(350470, hasMore);
	}

	@Test
	public final void testParseHasMoreFalse1() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Test_wp1_10_fr.htm");
		boolean hasMore = gl.parseHasMore(text1);
		assertEquals(false, hasMore);
	}

	@Test
	public final void testParseHasMoreFalse2() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Ohjelmistotuottanto_wp1_10_fi.htm");
		boolean hasMore = gl.parseHasMore(text1);
		assertEquals(false, hasMore);
	}
	
	@Test
	public final void testNumberOfWhatLinks1() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Test_wp1_10_de.htm");
		int has = gl.getArticles(text1).size();
		assertEquals(61, has);
		
	}
	@Test
	public final void testEncodingOfElements1() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Test_wp1_10_de.htm");
		boolean bool = gl.getArticles(text1).contains("Reliabilität");
		assertEquals(true, bool);
		
	}
	@Test
	public final void testEncodingOfElements2() {
		Node text1 = getText(gl, TestFileLoader.MW110
				+ "whatLinks2Test_wp1_10_de.htm");
		boolean bool = gl.getArticles(text1).contains("Dyskalkulie");
		assertEquals(true, bool);
		
	}

	private static Node getText(GetWhatlinkshereElements gl, String file) {
		return gl.getMainContent(TestFileLoader.readFromFile(file));
	}

}
