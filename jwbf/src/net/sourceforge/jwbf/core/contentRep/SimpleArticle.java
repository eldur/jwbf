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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * This is a simple content helper class that implements the
 * EditContentAccesable interface, plus setter methods.
 *
 * @author Thomas Stock
 *
 */
public class SimpleArticle implements ArticleMeta, Serializable, Cloneable, ContentSetable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1368796410854055279L;
	private String title = "";
	private String editSummary = "";
	private String text = "";
	private String editor = "";
	private boolean minorEdit = false;
	private Date editTimestamp = new Date();
	private String revId = "";

	/**
	 *
	 *
	 */
	public SimpleArticle() {
		// do nothing
	}

	/**
	 *
	 * @param ca
	 *            a
	 */
	public SimpleArticle(ContentAccessable ca) {

		if (ca.getTitle() != null) {
			title = ca.getTitle();
		}
		if (ca.getText() != null) {
			text = ca.getText();
		}
		if (ca.getEditSummary() != null) {
			editSummary = ca.getEditSummary();
		}
		if (ca.getEditor() != null) {
			editor = ca.getEditor();
		}

		setMinorEdit(ca.isMinorEdit());

	}

	/**
	 *
	 * @param sa
	 *            a
	 */
	public SimpleArticle(ArticleMeta sa) {
		this((ContentAccessable) sa);

		if (sa.getEditTimestamp() != null) {
			editTimestamp = sa.getEditTimestamp();
		}

		if (sa.getRevisionId() != null) {
			revId = sa.getRevisionId();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Object clone() throws CloneNotSupportedException {
	    super.clone();
		return new SimpleArticle(this);
	}

	/**
	 *
	 * @param text
	 *            of article
	 * @param title
	 *            of article
	 * @deprecated use {@link #SimpleArticle(String)} and
	 *             {@link #setText(String)} instead.
	 */
	@Deprecated
    public SimpleArticle(final String text, final String title) {
		this.text = text;
		this.title = title;
	}

	/**
	 *
	 * @param title
	 *            of article
	 */
	public SimpleArticle(final String title) {
		this.title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEditSummary() {
		return editSummary;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#setEditSummary(java.lang.String)
	 */
	public void setEditSummary(final String s) {
		this.editSummary = s;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isMinorEdit() {
		return minorEdit;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#setMinorEdit(boolean)
	 */
	public void setMinorEdit(final boolean minor) {
		this.minorEdit = minor;
	}

	/**
	 * @return the
	 * @deprecated use {@link #getTitle()} instead
	 */
	@Deprecated
    public String getLabel() {
		return getTitle();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitle() {
		return title;
	}

	/**
	 *
	 * @param label
	 *            the label, like "Main Page"
	 * @deprecated use {@link #setTitle(String)} instead
	 */
	@Deprecated
    public void setLabel(final String label) {
		setTitle(label);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#setTitle(java.lang.String)
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#setText(java.lang.String)
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#addText(java.lang.String)
	 */
	public void addText(final String text) {
		setText(getText() + text);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#addTextnl(java.lang.String)
	 */
	public void addTextnl(final String text) {
		setText(getText() + "\n" + text);

	}

	/**
	 * {@inheritDoc}
	 */
	public String getEditor() {
		return editor;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.jwbf.core.contentRep.ContentSetable#setEditor(java.lang.String)
	 */
	public void setEditor(final String editor) {
		this.editor = editor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRedirect() {

		Pattern pattern = Pattern.compile("#(.*)redirect(.*)",
				Pattern.CASE_INSENSITIVE);
		if (pattern.matcher(text).matches()) {
			return true;
		}

		return false;
	}

	/**
	 * @return the edittimestamp in UTC
	 */
	public Date getEditTimestamp() {
		return editTimestamp;
	}

	/**
	 *
	 * @param editTimestamp
	 *            set
	 * @throws ParseException
	 *             if date unparseable
	 */
	public void setEditTimestamp(String editTimestamp) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		try {
			setEditTimestamp(sdf.parse(editTimestamp));
		} catch (ParseException e) {
			sdf = new SimpleDateFormat("MM/dd/yy' 'HH:mm:ss");
			setEditTimestamp(sdf.parse(editTimestamp));
		}
	}

	/**
	 *
	 * @param d
	 *            the
	 */
	public void setEditTimestamp(Date d) {
		this.editTimestamp = d;
	}

	/* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof SimpleArticle))
      return false;
    SimpleArticle other = (SimpleArticle) obj;
    if (editTimestamp == null) {
      if (other.editTimestamp != null)
        return false;
    } else if (!editTimestamp.equals(other.editTimestamp))
      return false;
    if (revId == null) {
      if (other.revId != null)
        return false;
    } else if (!revId.equals(other.revId))
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    return true;
  }

	/* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((editTimestamp == null) ? 0 : editTimestamp.hashCode());
    result = prime * result + ((revId == null) ? 0 : revId.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    return result;
  }

	/**
	 * {@inheritDoc}
	 */
	public String getRevisionId() {
		return revId;
	}

	/**
	 *
	 * @param revId
	 *            the
	 */
	public void setRevisionId(String revId) {
		this.revId = revId;
	}

}
