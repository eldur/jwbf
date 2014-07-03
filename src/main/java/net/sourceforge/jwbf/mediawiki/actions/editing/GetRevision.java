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

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.ApiException;
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

  private final SimpleArticle sa;

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
   * TODO follow redirects. TODO change constructor fild ordering; bot
   */
  public GetRevision(Version v, final String articlename, final int properties) {
    this.properties = properties;
    sa = new SimpleArticle();
    sa.setTitle(articlename);
    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("prop", "revisions") //
        .param("titles", MediaWiki.urlEncode(articlename)) //
        .param("rvprop", getDataProperties(properties) + getReversion(properties)) //
        .param("rvlimit", "1") //
        .buildGet();
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
    XmlElement root = getRootElementWithError(xml);
    XmlElement error = getErrorElement(root);
    if (error != null) {
      throw new ApiException(error.getAttributeValue("code") //
          , error.getAttributeValue("info"));
    }
    findContent(root);
  }

  public SimpleArticle getArticle() {
    return sa;
  }

  private void findContent(final XmlElement root) {

    for (XmlElement xmlElement : root.getChildren()) {
      if (xmlElement.getQualifiedName().equalsIgnoreCase("rev")) {

        try {
          sa.setText(xmlElement.getText());
        } catch (NullPointerException e) {
          if (log.isDebugEnabled()) {
            log.debug("no text found");
          }
        }
        if ((properties & FLAGS) > 0) {
          if (xmlElement.hasAttribute("minor")) {
            sa.setMinorEdit(true);
          } else {
            sa.setMinorEdit(false);
          }
        }

        sa.setRevisionId(getAttrValueOf(xmlElement, "revid"));
        sa.setEditSummary(getAttrValueOf(xmlElement, "comment"));
        sa.setEditor(getAttrValueOf(xmlElement, "user"));

        if ((properties & TIMESTAMP) > 0) {
          sa.setEditTimestamp(getAttrValueOf(xmlElement, "timestamp"));
        }
      } else {
        findContent(xmlElement);
      }
    }

  }

  private String getAttrValueOf(XmlElement xmlElement, String key) {
    return getAttrValueOf(xmlElement, key, "");
  }

  private String getAttrValueOf(XmlElement xmlElement, String key, String otherwise) {
    String value = xmlElement.getAttributeValue(key);
    if (value == null) {
      log.trace("no value for {}", key);
      return otherwise;
    }

    log.trace("value for {}= \"{}\"", key, value);
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

}
