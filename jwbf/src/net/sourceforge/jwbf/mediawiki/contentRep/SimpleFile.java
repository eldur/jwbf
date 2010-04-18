/*
 * Copyright 2007 Justus Bisser.
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
package net.sourceforge.jwbf.mediawiki.contentRep;

import java.io.File;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

/**
 * This is a simple content helper class that implements the
 * EditContentAccesable interface, plus setter methods.
 * The field Text from SimpleArticle can be used as a description for the file.
 *
 * @author Justus Bisser
 *
 */
public class SimpleFile extends SimpleArticle {

	/**
	 *
	 */
	private static final long serialVersionUID = 90640839252699902L;
	private File filename;

	/**
	 *
	 * @param label new filename
	 * @param filename local filename
	 */
	public SimpleFile(final String label, String filename) {
		setText("");
		setTitle(label);
		this.filename = new File(filename);
	}

	/**
	 *
	 * @param label new filename
	 * @param filename local filename
	 */
	public SimpleFile(final String label, File filename) {
		setText("");
		setTitle(label);
		this.filename = filename;
	}

	/**
	 *
	 * @param filename local filename
	 */
	public SimpleFile(File filename) {
		setText("");
		setTitle(filename.getName());
		this.filename = filename;
	}

	/**
	 *
	 * @param filename local filename
	 */
	public SimpleFile(String filename) {
		setText("");
		this.filename = new File(filename);
		setTitle(this.filename.getName());
	}

	/**
	 *
	 * @return the
	 */
	public String getFilename() {
		return filename.getPath();
	}
	/**
	 *
	 * @return the
	 */
	public File getFile() {
		return this.filename;
	}

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((filename == null) ? 0 : filename.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof SimpleFile))
      return false;
    SimpleFile other = (SimpleFile) obj;
    if (filename == null) {
      if (other.filename != null)
        return false;
    } else if (!filename.equals(other.filename))
      return false;
    return true;
  }


}
