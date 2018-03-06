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
import java.util.Objects;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

/**
 * This is a simple content helper class that implements the EditContentAccesable interface, plus
 * setter methods. The field Text from SimpleArticle can be used as a description for the file.
 *
 * @author Justus Bisser
 */
public class SimpleFile extends SimpleArticle {

  private final File file;

  /**
   * @param label new file
   * @param file local file
   */
  public SimpleFile(final String label, String file) {
    this(label, new File(file));
  }

  /**
   * @param label new file
   * @param file local file
   */
  public SimpleFile(final String label, File file) {
    setText("");
    setTitle(label);
    this.file = file;
  }

  /** @param file local file */
  public SimpleFile(File file) {
    this(file.getName(), file);
  }

  /** @param filename local file */
  public SimpleFile(String filename) {
    this(new File(filename));
  }

  /** @return the */
  public File getFile() {
    return this.file;
  }

  public boolean isFile() {
    return getFile().isFile();
  }

  public boolean canRead() {
    return getFile().canRead();
  }

  public boolean exists() {
    return getFile().exists();
  }

  public String getPath() {
    return file.getPath();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(file, super.hashCode());
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SimpleFile) {
      SimpleFile that = (SimpleFile) obj;
      return super.equals(that) && Objects.equals(this.file, that.file);
    } else {
      return false;
    }
  }
}
