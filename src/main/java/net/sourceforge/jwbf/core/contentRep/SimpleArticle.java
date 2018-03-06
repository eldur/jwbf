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
package net.sourceforge.jwbf.core.contentRep;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import net.sourceforge.jwbf.core.internal.TimeConverter;

/**
 * This is a simple content helper class that implements the EditContentAccesable interface, plus
 * setter methods.
 *
 * @author Thomas Stock
 */
public class SimpleArticle implements ArticleMeta, Serializable, ContentSetable {

  private static final long serialVersionUID = -1368796410854055279L;
  private String title = "";
  private String editSummary = "";
  private String text = "";
  private String editor = "";
  private boolean minorEdit = false;
  private long editTimestamp = newZeroDate().getTime();
  private String revId = "";

  private static Pattern redirectPattern =
      Pattern //
          .compile(
          "#(.*)redirect (.*)" //
          ,
          Pattern.CASE_INSENSITIVE);

  @VisibleForTesting
  public static Date newZeroDate() {
    return new Date(0);
  }

  /** */
  public SimpleArticle() {
    // do nothing
  }

  public SimpleArticle(ContentAccessable ca) {
    if (ca instanceof Article) {
      throw new IllegalArgumentException(
          "do not convert an "
              + //
              Article.class.getCanonicalName()
              + " to a "
              + //
              getClass().getCanonicalName()
              + ", because its very expensive");
    }
    title = Optional.fromNullable(ca.getTitle()).or("");
    text = Optional.fromNullable(ca.getText()).or("");
    editSummary = Optional.fromNullable(ca.getEditSummary()).or("");
    editor = Optional.fromNullable(ca.getEditor()).or("");

    setMinorEdit(ca.isMinorEdit());
  }

  public SimpleArticle(ArticleMeta sa) {
    this((ContentAccessable) sa);

    if (sa.getEditTimestamp() != null) {
      editTimestamp = sa.getEditTimestamp().getTime();
    }

    if (sa.getRevisionId() != null) {
      revId = sa.getRevisionId();
    }
  }

  /**
   * @param text of article
   * @param title of article
   * @deprecated use {@link #SimpleArticle(String)} and {@link #setText(String)} instead.
   */
  @Deprecated
  public SimpleArticle(final String text, final String title) {
    this(title);
    this.text = text;
  }

  /** @param title of article */
  public SimpleArticle(final String title) {
    this.title = title;
  }

  /** {@inheritDoc} */
  @Override
  public String getEditSummary() {
    return editSummary;
  }

  @Override
  public void setEditSummary(final String s) {
    editSummary = s;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isMinorEdit() {
    return minorEdit;
  }

  @Override
  public void setMinorEdit(final boolean minor) {
    minorEdit = minor;
  }

  /**
   * @return the
   * @deprecated use {@link #getTitle()} instead
   */
  @Deprecated
  public String getLabel() {
    return getTitle();
  }

  /** {@inheritDoc} */
  @Override
  public String getTitle() {
    return title;
  }

  /**
   * @param label the label, like "Main Page"
   * @deprecated use {@link #setTitle(String)} instead
   */
  @Deprecated
  public void setLabel(final String label) {
    setTitle(label);
  }

  @Override
  public void setTitle(final String title) {
    this.title = title;
  }

  /** {@inheritDoc} */
  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setText(final String text) {
    this.text = text;
  }

  // TODO check mutation
  @Override
  public void addText(final String text) {
    setText(getText() + text);
  }

  // TODO check mutation
  @Override
  public void addTextnl(final String text) {
    setText(getText() + "\n" + text);
  }

  @Override
  public String getEditor() {
    return editor;
  }

  @Override
  public void setEditor(final String editor) {
    this.editor = editor;
  }

  /** doesn't work correct */
  @Beta
  @Override
  public boolean isRedirect() {
    if (redirectPattern.matcher(text).matches()) {
      return true;
    }
    return false;
  }

  @Override
  public Date getEditTimestamp() {
    return new Date(editTimestamp);
  }

  public void setEditTimestamp(String editTimestamp) {
    Date parsedDate = tryParse(editTimestamp);
    setEditTimestamp(parsedDate);
  }

  private Date tryParse(String editTimestamp) {
    Optional<Date> parsedDate = //
        TimeConverter.from(editTimestamp, TimeConverter.YYYYMMDD_T_HHMMSS_Z);
    return parsedDate.or(TimeConverter.from(editTimestamp, "MM/dd/yy' 'HH:mm:ss")).get();
  }

  public void setEditTimestamp(@Nullable Date d) {
    if (d != null) {
      editTimestamp = d.getTime();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SimpleArticle) {
      SimpleArticle that = (SimpleArticle) obj;
      return Objects.equals(this.editTimestamp, that.editTimestamp)
          && //
          Objects.equals(this.revId, that.revId)
          && //
          Objects.equals(this.text, that.text)
          && //
          Objects.equals(this.title, that.title) //
      ;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this) //
        .add("title", title) //
        .add("editSummary", editSummary) // XXX check equals
        .add("text", text) //
        .add("editor", editor) // XXX check equals
        .add("minorEdit", minorEdit) // XXX check equals
        .add("editTimestamp", editTimestamp) //
        .add("revId", revId) //
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(editTimestamp, revId, text, title);
  }

  /** {@inheritDoc} */
  @Override
  public String getRevisionId() {
    return revId;
  }

  /** @param revId the */
  public void setRevisionId(String revId) {
    this.revId = revId;
  }
}
