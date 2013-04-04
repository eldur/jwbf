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
package net.sourceforge.jwbf.inyoka.actions;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.ContentProcessable;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;

/**
 * Reads the content of a given article.
 * 
 * @author Thomas Stock
 * 
 * @supportedBy Inyoka ??? TODO find out version
 * 
 */
@Slf4j
public class GetRevision implements ContentProcessable {

  private final SimpleArticle sa;

  private boolean first = true;
  private boolean second = true;
  private boolean third = true;
  private final Get contentGet;
  private Get metaGet;
  private Get versionGet;
  private int version = 0;

  /**
   * TODO follow redirects.
   */
  public GetRevision(final String articlename) {
    if (articlename.length() <= 0) {
      throw new ProcessException("articlename is empty");
    }
    sa = new SimpleArticle();
    sa.setTitle(articlename);

    contentGet = new Get("/" + articlename + "?action=export&format=raw&");
    versionGet = new Get("/" + articlename);
    if (log.isDebugEnabled()) {
      log.debug(contentGet.getRequest());
      log.debug(versionGet.getRequest());
    }

  }

  public String processReturningText(String s, HttpAction hm) {
    if (hm == contentGet) {
      sa.setText(s);
    } else if (hm == versionGet) {
      parseVersion(s);
      metaGet = new Get("/" + sa.getTitle() + "?action=diff&version=" + version);

    } else if (hm == metaGet) {
      parse(s);
    }
    return "";
  }

  private static final Pattern authorPattern = Pattern.compile("class=\"author\">([^\"]*)<",
      Pattern.DOTALL | Pattern.MULTILINE);
  private static final Pattern editTimestampPattern = Pattern.compile("class=\"time\">([^\"]*)<",
      Pattern.DOTALL | Pattern.MULTILINE);
  private static final Pattern editMessagePattern = Pattern.compile(
      "class=\"message\"><p>([^\"]*)</p>", Pattern.DOTALL | Pattern.MULTILINE);
  private static final Pattern versionPattern = Pattern.compile("action=diff&amp;version=([0-9]*)",
      Pattern.DOTALL | Pattern.MULTILINE);

  private void parse(String s) {
    // System.err.println(s); // TODO RM

    Matcher m = authorPattern.matcher(s);

    if (m.find()) {
      sa.setEditor(m.group(1).trim());
    }
    // find edittimestamp
    m = editTimestampPattern.matcher(s);

    if (m.find()) {

      try {
        sa.setEditTimestamp(m.group(1).trim());

      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    } else {
      throw new RuntimeException("no date found"); // TODO RM
    }
    // find edit summ
    m = editMessagePattern.matcher(s);

    if (m.find()) {

      sa.setEditSummary(m.group(1).trim());

    } else {
      throw new RuntimeException("no edit sum found found"); // TODO RM
    }
  }

  private void parseVersion(String s) {

    Matcher m = versionPattern.matcher(s);

    if (m.find()) {
      version = Integer.parseInt(m.group(1));
    }
  }

  public SimpleArticle getArticle() {
    return sa;
  }

  public boolean hasMoreMessages() {
    if (first || second || third)
      return true;
    return false;
  }

  public HttpAction getNextMessage() {
    if (first) {
      first = false;
      return contentGet;
    } else if (second) {
      second = false;
      return versionGet;
    } else {
      third = false;
      return metaGet;
    }

  }

  /**
   * {@inheritDoc}
   */
  public boolean isSelfExecuter() {
    return false;
  }

}
