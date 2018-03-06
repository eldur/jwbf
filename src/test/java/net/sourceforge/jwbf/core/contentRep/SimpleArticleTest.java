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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Philipp Kohl
 * @author Thomas Stock Simple Test-Case to get Unit-Testing started
 */
public class SimpleArticleTest {

  private SimpleArticle article;

  /** setup. */
  @Before
  public void setUp() {

    article = new SimpleArticle();
  }

  /** Tests edit summary. */
  @Test
  public void testEditSummary() {
    article.setEditSummary("test");
    assertEquals("test", article.getEditSummary());
  }

  @Test
  public void testDateFormat() {
    // GIVEN
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    String dateString = "2007-01-08T15:12:55Z";

    // WHEN
    article.setEditTimestamp(dateString);

    // THEN
    assertEquals(dateString, sdf.format(article.getEditTimestamp()));
  }

  @Test
  public void testDateFormatTrac() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

    String dateString = "02/04/09 14:04:36 (35 minutes ago)</dd>\n\n      \n";
    String dateStringTarget = "2009-02-04T14:04:36Z";
    article.setEditTimestamp(dateString);

    assertEquals(dateStringTarget, sdf.format(article.getEditTimestamp()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInitWithArticle() {
    new SimpleArticle(mock(Article.class));
    fail();
  }

  @Test
  public void testInitWithArticleMeta() {
    SimpleArticle sa =
        new SimpleArticle(
            new MetaAdapter() {

              @Override
              public boolean isRedirect() {
                return false;
              }

              @Override
              public Date getEditTimestamp() {
                return null;
              }

              @Override
              public String getRevisionId() {
                return null;
              }
            });

    // THEN
    assertEquals("", sa.getEditor());
    assertEquals("", sa.getEditSummary());
    assertEquals("", sa.getTitle());
    assertEquals("", sa.getText());
    assertNotNull(sa.getEditTimestamp());
  }

  @Test
  public void testInitWithArticleMeta_nonnull() {
    SimpleArticle sa =
        new SimpleArticle(
            new MetaAdapter() {

              @Override
              public boolean isRedirect() {
                return false;
              }

              @Override
              public Date getEditTimestamp() {
                return new Date(7);
              }

              @Override
              public String getRevisionId() {
                return "5";
              }
            });

    // THEN
    assertEquals("", sa.getEditor());
    assertEquals("", sa.getEditSummary());
    assertEquals("", sa.getTitle());
    assertEquals("", sa.getText());
    assertEquals("5", sa.getRevisionId());
    assertEquals(new Date(7), sa.getEditTimestamp());
  }

  @Test
  public void testNoNullpointer() {
    ContentAccessable ca =
        new ContentAccessable() {

          @Override
          public boolean isMinorEdit() {
            return false;
          }

          @Override
          public String getText() {
            return null;
          }

          @Override
          public String getTitle() {
            return null;
          }

          @Override
          public String getEditor() {
            return null;
          }

          @Override
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
    assertEquals("", sa.getTitle());
    assertEquals("", sa.getText());
    assertNotNull(sa.getEditTimestamp());
  }

  @Test
  public void testNoNullpointer1() {
    ContentAccessable ca =
        new ContentAccessable() {

          @Override
          public boolean isMinorEdit() {
            return false;
          }

          @Override
          public String getText() {
            return "test";
          }

          @Override
          public String getTitle() {
            return "MyTest";
          }

          @Override
          public String getEditor() {
            return null;
          }

          @Override
          public String getEditSummary() {
            return null;
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
  public void testInit_deprecated() {
    SimpleArticle sa = new SimpleArticle("a", "b");

    assertEquals("b", sa.getTitle());
    assertEquals("a", sa.getText());
  }

  @Test
  public void testInit_with_title() {
    SimpleArticle sa = new SimpleArticle("a");

    assertEquals("a", sa.getTitle());
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
  @Ignore
  public void testIsRedirectLocale() {
    article.setText("#WEITERLEITUNG [[A]]");
    assertTrue(article.isRedirect());
    article.setText("#REDIRECTION [[A]]");
    assertTrue(article.isRedirect());
    article.setText("#REDIRECCIÓN [[A]]");
    assertTrue(article.isRedirect());
    article.setText("#REDIRECCION [[A]]");
    assertTrue(article.isRedirect());
  }

  @Test
  public void testIsRedirectFail() {
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
    Date d3 = new Date(System.currentTimeMillis() + 10000);

    SimpleArticle sa = new SimpleArticle();
    assertTrue("self", sa.equals(sa));
    assertFalse("null", sa.equals(null));
    assertFalse("other object", sa.equals(new Object()));
    SimpleArticle simpleArticle = new SimpleArticle();
    assertTrue("init", sa.equals(simpleArticle));
    simpleArticle.setTitle(null);
    assertFalse("null title sax", sa.equals(simpleArticle));
    sa.setTitle(null);
    assertTrue("null title both", sa.equals(simpleArticle));
    simpleArticle.setTitle("d");
    assertFalse("null title sa", sa.equals(simpleArticle));
    sa.setTitle("A");
    assertFalse("2", sa.equals(simpleArticle));
    simpleArticle.setTitle("A");
    assertTrue("3", sa.equals(simpleArticle));
    simpleArticle.setTitle("B");
    assertFalse("4", sa.equals(simpleArticle));
    simpleArticle.setTitle("A");
    simpleArticle.setText("ABC");
    assertFalse("5", sa.equals(simpleArticle));
    sa.setText("ABC");
    assertTrue("6", sa.equals(simpleArticle));
    sa.setText(null);
    assertFalse("sa text null", sa.equals(simpleArticle));
    simpleArticle.setText(null);
    assertTrue("both text null", sa.equals(simpleArticle));
    sa.setText("s");
    assertFalse("sax text null", sa.equals(simpleArticle));
    simpleArticle.setText("s");
    simpleArticle.setEditTimestamp(d2);
    assertFalse("7", sa.equals(simpleArticle));
    Date nullDate = null;
    sa.setEditTimestamp(nullDate);
    assertFalse("null time", sa.equals(simpleArticle));
    sa.setEditTimestamp(d3);
    assertFalse("8", sa.equals(simpleArticle));
    sa.setEditTimestamp(d2);
    assertTrue("9", sa.equals(simpleArticle));
    sa.setRevisionId(null);
    assertFalse("sa rev null", sa.equals(simpleArticle));
    simpleArticle.setRevisionId(null);
    assertTrue("both rev null", sa.equals(simpleArticle));
    sa.setRevisionId("sf");
    assertFalse("sax rev null", sa.equals(simpleArticle));
    simpleArticle.setRevisionId("sff");
    assertFalse("sax rev diff", sa.equals(simpleArticle));
  }

  @Test
  public void testNewZeroDate() {
    // GIVEN / WHEN
    DateTime actual = new DateTime(SimpleArticle.newZeroDate());

    // THEN
    assertEquals(0, actual.getMillis());
  }

  private abstract static class MetaAdapter implements ArticleMeta {

    @Override
    public String getEditSummary() {
      return null;
    }

    @Override
    public String getEditor() {
      return null;
    }

    @Override
    public boolean isMinorEdit() {
      return false;
    }

    @Override
    public String getTitle() {
      return null;
    }

    @Override
    public String getText() {
      return null;
    }
  }
}
