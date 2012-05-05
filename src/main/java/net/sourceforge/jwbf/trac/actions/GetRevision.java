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
package net.sourceforge.jwbf.trac.actions;

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
 * @supportedBy Trac ??? TODO find out version
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
   * @param articlename a
   * @throws ProcessException if arcticlename is empty
   */
  public GetRevision(final String articlename) throws ProcessException {
    if (articlename.length() <= 0) {
      throw new ProcessException("articlename is empty");
    }
    sa = new SimpleArticle();
    sa.setTitle(articlename);


    contentGet = new Get("/" + articlename + "?format=txt");
    versionGet = new Get("/" + articlename);
    if (log.isDebugEnabled()) {
      log.debug(contentGet.getRequest());
      log.debug(versionGet.getRequest());
    }

  }


  public String processReturningText(String s, HttpAction hm)
      throws ProcessException {
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

  private void parse(String s) {
    //		 <dt class="property author">Author:</dt>
    //		   <dd class="author">anonymous <span class="ipnr">(IP: 219.232.117.132)</span></dd>
    //		   <dt class="property time">Timestamp:</dt>
    //		   <dd class="time">02/04/09 01:49:20 (12 hours ago)</dd>
    //		   <dt class="property message">Comment:</dt>
    //		   <dd class="message"><p>
    Pattern p = Pattern.compile("class=\"author\">([^\"]*)<",
        Pattern.DOTALL | Pattern.MULTILINE);

    Matcher m = p.matcher(s);

    if (m.find()) {
      sa.setEditor(m.group(1).trim());
    }
    // find edittimestamp
    p = Pattern.compile("class=\"time\">([^\"]*)<", Pattern.DOTALL
        | Pattern.MULTILINE);
    m = p.matcher(s);

    if (m.find()) {

      try {
        sa.setEditTimestamp(m.group(1).trim());

      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else {
      System.err.println("no date found");
    }
    // find edit summ
    p = Pattern.compile("class=\"message\"><p>([^\"]*)</p>", Pattern.DOTALL
        | Pattern.MULTILINE);
    m = p.matcher(s);

    if (m.find()) {

      sa.setEditSummary(m.group(1).trim());

    } else {
      System.err.println("no edit sum found found");
    }
  }

  private void parseVersion(String s) {
    Pattern p = Pattern.compile("action=diff&amp;version=([0-9]*)"
        , Pattern.DOTALL | Pattern.MULTILINE);

    Matcher m = p.matcher(s);

    if (m.find()) {
      version = Integer.parseInt(m.group(1));
    }
  }

  public SimpleArticle getArticle() {
    return sa;
  }


  public boolean hasMoreMessages() {
    if (first || second || third )
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
    } else  {
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
