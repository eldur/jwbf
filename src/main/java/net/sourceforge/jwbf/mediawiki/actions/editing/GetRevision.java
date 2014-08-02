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
package net.sourceforge.jwbf.mediawiki.actions.editing;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads the content of a given article.
 *
 * @author Thomas Stock
 */
public class GetRevision extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(GetRevision.class);

  private SimpleArticle sa;

  public static final int CONTENT = 1 << 1;
  public static final int TIMESTAMP = 1 << 2;
  public static final int USER = 1 << 3;
  public static final int COMMENT = 1 << 4;
  public static final int IDS = 1 << 5;
  public static final int FLAGS = 1 << 6;

  public static final int FIRST = 1 << 30;
  public static final int LAST = 1 << 31;

  private final int properties;

  private final Get msg;

  private boolean singleProcess = true;

  /**
   * TODO follow redirects. TODO change constructor field ordering; bot
   */
  public GetRevision(Version v, String articlename, int properties) {
    this.properties = properties;
    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("prop", "revisions") //
        .param("titles", MediaWiki.urlEncode(articlename)) //
        .param("rvprop", getDataProperties(properties) + getReversion(properties)) //
        .param("rvlimit", "1") //
        .buildGet();
  }

  public GetRevision(ImmutableList<String> names, int properties) {
    this(null, MediaWiki.pipeJoined(names), properties);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(final String s, HttpAction ha) {
    if (msg.getRequest().equals(ha.getRequest()) && singleProcess) {
      if (log.isDebugEnabled()) { // TODO no very nice debug here
        if (s.length() < 151) {
          log.debug(s);
        } else {
          log.debug("..." + s.substring(50, 150) + "...");
        }
      }

      parse(s);
      singleProcess = false;

    }
    return "";
  }

  /**
   * TODO Not very nice implementation.
   */
  private String getDataProperties(final int property) {
    String properties = "";

    if ((property & CONTENT) > 0) {
      properties += "content|";
    }
    if ((property & COMMENT) > 0) {
      properties += "comment|";
    }
    if ((property & TIMESTAMP) > 0) {
      properties += "timestamp|";
    }
    if ((property & USER) > 0) {
      properties += "user|";
    }
    if ((property & IDS) > 0) {
      properties += "ids|";
    }
    if ((property & FLAGS) > 0) {
      properties += "flags|";
    }

    if (properties.length() > 0) {
      return MediaWiki.urlEncode(properties.substring(0, properties.length() - 1));
    }

    return "";
  }

  private String getReversion(final int property) {
    String properties = "&rvdir=";

    if ((property & FIRST) > 0) {
      properties += "newer";
    } else {
      properties += "older";
    }

    return properties;
  }

  private void parse(final String xml) {

    Optional<XmlElement> childOpt = XmlConverter.getChildOpt(xml, "query", "pages");
    if (childOpt.isPresent()) {
      List<XmlElement> pages = childOpt.get().getChildren("page");
      for (XmlElement page : pages) {
        sa = new SimpleArticle();
        sa.setTitle(page.getAttributeValue("title"));
        XmlElement rev = page.getChild("revisions").getChild("rev");
        sa.setText(rev.getText());
        sa.setRevisionId(rev.getAttributeValueOpt("revid").or(""));
        sa.setEditSummary(rev.getAttributeValueOpt("comment").or(""));
        sa.setEditor(rev.getAttributeValueOpt("user").or(""));
        if ((properties & TIMESTAMP) > 0) {
          sa.setEditTimestamp(rev.getAttributeValueOpt("timestamp").or(""));
        }
        if ((properties & FLAGS) > 0) {
          if (rev.hasAttribute("minor")) {
            sa.setMinorEdit(true);
          } else {
            sa.setMinorEdit(false);
          }
        }
      }
    }
  }

  public SimpleArticle getArticle() {
    return Iterables.getOnlyElement(asList());
  }

  public ImmutableList<SimpleArticle> asList() {
    return ImmutableList.of(sa); // TODO
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

}
