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
package net.sourceforge.jwbf.mediawiki.actions.util;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.MediaWiki;

/** @author Thomas Stock */
public abstract class MWAction implements ContentProcessable {

  private static final Logger log = LoggerFactory.getLogger(MWAction.class);

  /** @deprecated remove */
  @Deprecated private boolean hasMore = true;

  /** @return true if and changes state to false */
  @Override
  public boolean hasMoreMessages() {
    final boolean b = hasMore;
    hasMore = false;
    return b;
  }

  /** @deprecated do not use this method TODO rm */
  @Deprecated
  public void setHasMoreMessages(boolean b) {
    hasMore = b;
  }

  protected MWAction() {}

  /**
   * Deals with the MediaWiki API's response by parsing the provided text.
   *
   * @param s the answer to the most recently generated MediaWiki API request
   * @param hm the requestor message
   * @return the returning text on processing problems
   */
  @Override
  public String processReturningText(final String s, final HttpAction hm) {
    return processAllReturningText(s);
  }

  /**
   * @param s the returning text
   * @return the returning text never
   */
  public String processAllReturningText(final String s) {
    return s;
  }

  /**
   * helper method generating a namespace string as required by the MW-api.
   *
   * @param namespaces namespace as
   * @return with numbers seperated by |
   * @deprecated prefer {@link #createNsString(java.util.List)}
   */
  @Deprecated
  public static String createNsString(int... namespaces) {
    return MediaWiki.createNsString(nullSafeCopyOf(namespaces));
  }

  /** @deprecated */
  @Deprecated
  public static ImmutableList<String> nullSafeCopyOf(@Nullable String[] strings) {
    return MediaWiki.nullSafeCopyOf(strings);
  }

  /** @deprecated */
  @Deprecated
  @Nonnull
  public static ImmutableList<Integer> nullSafeCopyOf(@Nullable int[] ints) {
    return MediaWiki.nullSafeCopyOf(ints);
  }

  /** @deprecated */
  @Deprecated
  public static String createNsString(List<Integer> asList) {
    return MediaWiki.createNsString(asList);
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated see interface
   */
  @Override
  @Deprecated
  public boolean isSelfExecuter() {
    return false;
  }
}
