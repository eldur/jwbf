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

import java.text.ParseException;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.ApiException;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;

import org.jdom.Element;

/**
 * Reads the content of a given article.
 * 
 * @author Thomas Stock
 */
@Slf4j
public class GetRevision extends MWAction {

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
    super(v);
    // if (!bot.getUserinfo().getRights().contains("read")) {
    // throw new
    // ActionException("reading is not permited, make sure that this account is able to read");
    // } FIXME check if

    this.properties = properties;
    sa = new SimpleArticle();
    sa.setTitle(articlename);
    msg = new ApiRequestBuilder() //
        .action("query") //
        .formatXml() //
        .param("prop", "revisions") //
        .param("titles", MediaWiki.encode(articlename)) //
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
      return MediaWiki.encode(properties.substring(0, properties.length() - 1));
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
    Element root = getRootElementWithError(xml);
    Element error = getErrorElement(root);
    if (error != null) {
      throw new ApiException(error.getAttributeValue("code") //
          , error.getAttributeValue("info"));
    }
    findContent(root);
  }

  public SimpleArticle getArticle() {

    return sa;
  }

  private void findContent(final Element root) {

    @SuppressWarnings("unchecked")
    Iterator<Element> el = root.getChildren().iterator();
    while (el.hasNext()) {
      Element element = el.next();
      if (element.getQualifiedName().equalsIgnoreCase("rev")) {

        try {
          sa.setText(element.getText());
        } catch (NullPointerException e) {
          if (log.isDebugEnabled()) {
            log.debug("no text found");
          }
        }
        if ((properties & FLAGS) > 0) {
          if (element.getAttribute("minor") != null) {
            sa.setMinorEdit(true);
          } else {
            sa.setMinorEdit(false);
          }
        }

        sa.setRevisionId(getAttrValueOf(element, "revid"));
        sa.setEditSummary(getAttrValueOf(element, "comment"));
        sa.setEditor(getAttrValueOf(element, "user"));

        if ((properties & TIMESTAMP) > 0) {

          try {
            sa.setEditTimestamp(getAttrValueOf(element, "timestamp"));
          } catch (ParseException e) {
            log.debug("timestamp could not be parsed");
          }
        }

      } else {
        findContent(element);
      }

    }

  }

  private String getAttrValueOf(Element element, String key) {
    return getAttrValueOf(element, key, "");
  }

  private String getAttrValueOf(Element element, String key, String otherwise) {
    String value = null;

    value = element.getAttributeValue(key);
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
  public HttpAction getNextMessage() {
    return msg;
  }

}
