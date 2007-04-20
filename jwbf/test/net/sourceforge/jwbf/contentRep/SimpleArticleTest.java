package net.sourceforge.jwbf.contentRep;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Phil
 *
 * Simple Test-Case to get Unit-Testing started
 */
public class SimpleArticleTest {

	
	private SimpleArticle article;

	@Before
	protected void setUp() throws Exception {

		article = new SimpleArticle();
	}
	@Test
	public void testEditSummary() {
		article.setEditSummary("test");
		assertEquals("test", article.getEditSummary());
	}
}
