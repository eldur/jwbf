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
 * Thomas Stock
 */
package net.sourceforge.jwbf.contentRep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.junit.Before;
import org.junit.Test;

/**
 * @author Philipp Kohl 
 * @author Thomas Stock
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
	
	@Test
	public void testDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
        	
        	String dateString = "2007-01-08T15:12:55Z";
        	article.setEditTimestamp(dateString);
		
			
			assertEquals(dateString, sdf.format(article.getEditTimestamp()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDateFormatTrac() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
        	
        	String dateString = "02/04/09 14:04:36 (35 minutes ago)</dd>\n\n      \n";
        	String dateStringTarget = "2009-02-04T14:04:36Z";
        	article.setEditTimestamp(dateString);
		
			
			assertEquals(dateStringTarget, sdf.format(article.getEditTimestamp()));
	}

	
	@Test
	public void testNoNullpointer() {
		ContentAccessable ca = new ContentAccessable() {
		
			public boolean isMinorEdit() {
				return false;
			}
		
			public String getText() {
				return null;
			}
		
			public String getLabel() {
				return null;
			}
		
			public String getEditor() {
				return null;
			}
		
			public String getEditSummary() {
				return null;
			}

			public Date getEditTimestamp() {
				return null;
			}
		
		};
		
		SimpleArticle sa = new SimpleArticle(ca);
		assertEquals("", sa.getEditor());
		assertEquals("", sa.getEditSummary());
		assertEquals("", sa.getLabel());
		assertEquals("", sa.getText());
		Date d = sa.getEditTimestamp();
		assertTrue(d.getTime() > 0);
		
	}
	
	@Test
	public void testNoNullpointer1() {
		ContentAccessable ca = new ContentAccessable() {
		
			public boolean isMinorEdit() {
				return false;
			}
		
			public String getText() {
				return "test";
			}
		
			public String getLabel() {
				return "MyTest";
			}
		
			public String getEditor() {
				return null;
			}
		
			public String getEditSummary() {
				return null;
			}
		
		};
		
		SimpleArticle sa = new SimpleArticle(ca);
		
		assertEquals("", sa.getEditor());
		assertEquals("", sa.getEditSummary());
		assertEquals("MyTest", sa.getLabel());
		assertEquals("test", sa.getText());
	}
	@Test
	public void testNoNullpointer2() {
		SimpleArticle sa = new SimpleArticle();
		
		assertEquals("", sa.getEditor());
		assertEquals("", sa.getEditSummary());
		assertEquals("", sa.getLabel());
		assertEquals("", sa.getText());
		assertTrue(sa.getEditTimestamp().getTime() > 0);
		assertFalse(sa.isRedirect());
		assertFalse(sa.isMinorEdit());
		
		
	}
	
	@Test
	public void testIsRedirect() {
		article.setText("#redirect [[A]]");
		assertTrue(article.isRedirect());
		article.setText("#REDIRECT [[A]]");
		assertTrue(article.isRedirect());
		article.setText("# redirect [[A]]");
		assertTrue(article.isRedirect());
		article.setText("# redirect [[A]] [[Category:B]]");
		assertTrue(article.isRedirect());
	}
	@Test
	public void testIsNoRedirect() {
		article.setText("Text\n#redirect [[A]]");
		assertFalse(article.isRedirect());
		article.setText("Text #REDIRECT [[A]]");
		assertFalse(article.isRedirect());
		article.setText("Text# redirect [[A]]");
		assertFalse(article.isRedirect());
		article.setText("Text# redirect [[A]]");
		assertFalse(article.isRedirect());
	}
}
