package net.sourceforge.jwbf.contentRep;

import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;
import junit.framework.TestCase;

/**
 * @author Phil
 *
 * Simple Test-Case to get Unit-Testing started
 */
public class TestSimpleArticle extends TestCase {

	
	private SimpleArticle article;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		article=new SimpleArticle();
	}
	
	public void testEditSummary(){
		article.setEditSummary("test");
		assertEquals("test",article.getEditSummary());
	}
}
