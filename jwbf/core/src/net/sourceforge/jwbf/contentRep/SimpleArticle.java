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
package net.sourceforge.jwbf.contentRep;

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
public class SimpleArticle implements ArticleMeta, Serializable {

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
	 * 
	 * @param text
	 *            of article
	 * @param label
	 *            of article
	 */
	public SimpleArticle(final String text, final String label) {
		this.text = text;
		this.title = label;
	}
	
	/**
	 * 
	 * @param label
	 *            of article
	 */
	public SimpleArticle(final String label) {
		this("", label);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEditSummary() {
		return editSummary;
	}

	/**
	 * 
	 * @param s
	 *            the
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

	/**
	 * 
	 * @param minor
	 *            the
	 */
	public void setMinorEdit(final boolean minor) {
		this.minorEdit = minor;
	}

	/**
	 * @return the
	 * @deprecated use {@link #getTitle()} instead
	 */
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
	public void setLabel(final String label) {
		setTitle(label);
	}
	/**
	 * 
	 * @param title
	 *            the label, like "Main Page"
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

	/**
	 * 
	 * @param text
	 *            the content of the article
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * 
	 * @param text
	 *            to add to content of the article
	 */
	public void addText(final String text) {
		this.text += text;
	}

	/**
	 * 
	 * @param text
	 *            to add to content of the article
	 */
	public void addTextnl(final String text) {
		this.text += "\n" + text;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEditor() {
		return editor;
	}

	/**
	 * @param editor
	 *            the
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
	 * {@inheritDoc}
	 */
	public Date getEditTimestamp() {
		return editTimestamp;
	}
	/**
	 * 
	 * @param editTimestamp set
	 * @throws ParseException if date unparseable
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
	 * @param d the
	 */
	public void setEditTimestamp(Date d) {
		this.editTimestamp = d;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
            return true;
        
        if (!(obj instanceof SimpleArticle))
            return false;

        SimpleArticle sa = (SimpleArticle) obj;
		return sa.getTitle().equals(getTitle()) 
			&& sa.getText().equals(getText()) 
			&& sa.getEditTimestamp().equals(getEditTimestamp());
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRevisionId() {
		return revId;
	}
	/**
	 * 
	 * @param revId the
	 */
	public void setRevisionId(String revId) {
		this.revId = revId;
	}
	

	
}
