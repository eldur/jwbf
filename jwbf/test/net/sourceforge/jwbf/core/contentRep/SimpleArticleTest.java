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
package net.sourceforge.jwbf.core.contentRep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

      public String getTitle() {
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

      @Override
      public Object clone() throws CloneNotSupportedException {
        return this;
      }

    };

    SimpleArticle sa = new SimpleArticle(ca);
    assertEquals("", sa.getEditor());
    assertEquals("", sa.getEditSummary());
    assertEquals("", sa.getTitle());
    assertEquals("", sa.getText());
    assertNotNull(sa.getEditTimestamp());

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

      public String getTitle() {
        return "MyTest";
      }

      public String getEditor() {
        return null;
      }

      public String getEditSummary() {
        return null;
      }

      @Override
      public Object clone() throws CloneNotSupportedException {
        return this;
      }
    };

    SimpleArticle sa = new SimpleArticle(ca);

    assertEquals("", sa.getEditor());
    assertEquals("", sa.getEditSummary());
    assertEquals("MyTest", sa.getTitle());
    assertEquals("test", sa.getText());
  }
  @Test
  public void testNoNullpointer2() {
    SimpleArticle sa = new SimpleArticle();

    assertEquals("", sa.getEditor());
    assertEquals("", sa.getEditSummary());
    assertEquals("", sa.getTitle());
    assertEquals("", sa.getText());
    assertNotNull(sa.getEditTimestamp());
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

  @Test
  public void testHashCode() {
    SimpleArticle sa1 = new SimpleArticle();
    assertEquals(sa1.hashCode(), sa1.hashCode());
    SimpleArticle sa2 = new SimpleArticle();
    sa1.setTitle("a");
    sa2.setTitle("a");
    sa1.setText("a");
    sa2.setText("a");
    assertEquals(sa1.hashCode(), sa2.hashCode());
  }
  @Test
  public void testEquals() {
    Date d2 = new Date(System.currentTimeMillis());
    doWait(10);
    Date d3 = new Date(System.currentTimeMillis());

    SimpleArticle sa = new SimpleArticle();
    assertTrue("self", sa.equals(sa));
    assertFalse("null", sa.equals(null));
    assertFalse("other object", sa.equals(new Object()));
    SimpleArticle sax = new SimpleArticle();
    assertTrue("init", sa.equals(sax));
    sax.setTitle(null);
    assertFalse("null title sax", sa.equals(sax));
    sa.setTitle(null);
    assertTrue("null title both", sa.equals(sax));
    sax.setTitle("d");
    assertFalse("null title sa", sa.equals(sax));
    sa.setTitle("A");
    assertFalse("2", sa.equals(sax));
    sax.setTitle("A");
    assertTrue("3", sa.equals(sax));
    sax.setTitle("B");
    assertFalse("4", sa.equals(sax));
    sax.setTitle("A");
    sax.setText("ABC");
    assertFalse("5", sa.equals(sax));
    sa.setText("ABC");
    assertTrue("6", sa.equals(sax));
    sa.setText(null);
    assertFalse("sa text null", sa.equals(sax));
    sax.setText(null);
    assertTrue("both text null", sa.equals(sax));
    sa.setText("s");
    assertFalse("sax text null", sa.equals(sax));
    sax.setText("s");
    sax.setEditTimestamp(d2);
    assertFalse("7", sa.equals(sax));
    Date nullDate = null;
    sa.setEditTimestamp(nullDate);
    assertFalse("null time", sa.equals(sax));
    sa.setEditTimestamp(d3);
    assertFalse("8", sa.equals(sax));
    sa.setEditTimestamp(d2);
    assertTrue("9", sa.equals(sax));
    sa.setRevisionId(null);
    assertFalse("sa rev null", sa.equals(sax));
    sax.setRevisionId(null);
    assertTrue("both rev null", sa.equals(sax));
    sa.setRevisionId("sf");
    assertFalse("sax rev null", sa.equals(sax));
    sax.setRevisionId("sff");
    assertFalse("sax rev diff", sa.equals(sax));
  }

  private synchronized void doWait(int ms) {
    try {
      wait(ms);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
