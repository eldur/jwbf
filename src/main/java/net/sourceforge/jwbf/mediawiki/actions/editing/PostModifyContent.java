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

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.RequestBuilder;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.ContentAccessable;
import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.actions.util.VersionException;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * Writes an article.
 * 
 * @author Thomas Stock
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class PostModifyContent extends MWAction {

  private boolean first = true;
  private boolean second = true;

  private final ContentAccessable a;
  private MediaWikiBot bot;
  private GetApiToken apiReq = null;
  private HttpAction apiGet = null;
  private Post postModify = null;
  static final String PARAM_MINOR = "minor";
  static final String PARAM_MINOR_NOT = "notminor";
  static final String PARAM_BOTEDIT = "bot";

  public PostModifyContent(MediaWikiBot bot, final SimpleArticle a) {
    super(bot.getVersion());
    if (Strings.isNullOrEmpty(a.getTitle())) {
      throw new ActionException("imposible request, no title");
    }
    this.a = a;
    this.bot = bot;
  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {

    Userinfo userinfo = bot.getUserinfo();
    Set<String> rights = userinfo.getRights();
    boolean canWrite = (rights.contains(Userinfo.RIGHT_EDIT) && rights
        .contains(Userinfo.RIGHT_WRITEAPI));
    if (!(bot.isEditApi() && canWrite)) {
      throw new VersionException("editing is not allowed");
    }
    if (first) {
      first = false;
      apiReq = new GetApiToken(GetApiToken.Intoken.EDIT, a.getTitle(), bot.getVersion(), userinfo);
      apiGet = apiReq.getNextMessage();
      return apiGet;
    } else if (second) {

      postModify = new RequestBuilder(MediaWiki.URL_API) //
          .param("action", "edit") //
          .param("format", "xml") //
          .param("title", MediaWiki.encode(a.getTitle())) //
          .buildPost();
      postModify.addParam("summary", a.getEditSummary());
      postModify.addParam("text", a.getText());
      Set<String> groups = userinfo.getGroups();
      if (!isIntersectionEmpty(groups, MediaWiki.BOT_GROUPS)) {
        postModify.addParam(PARAM_BOTEDIT, "");
      }

      // postModify.addParam("watch", "unknown")
      if (a.isMinorEdit()) {
        postModify.addParam(PARAM_MINOR, "");
      } else {
        postModify.addParam(PARAM_MINOR_NOT, "");
      }
      postModify.addParam("token", apiReq.getToken());

      second = false;

      return postModify;
    } else {
      throw new IllegalStateException("this action has only two messages");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasMoreMessages() {
    return first || second;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processReturningText(String xml, HttpAction hm) {
    String request = hm.getRequest();
    if (request.equals(apiGet.getRequest())) {
      apiReq.processReturningText(xml, hm);
    } else if (request.equals(postModify.getRequest())) {
      getRootElement(xml);
    } else {
      log.trace(xml);
      throw new ActionException("unknown response");
    }

    return xml;
  }

  /**
   * @return true if one or both sets are <code>null</code> or the intersection of sets is empty.
   */
  boolean isIntersectionEmpty(Set<?> a, Set<?> b) {
    if (a == b) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    SetView<?> intersection = Sets.intersection(a, b);
    return intersection.isEmpty();
  }

}
