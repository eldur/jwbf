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
 * Philipp Kohl
 * Carlos Valenzuela
 */
package net.sourceforge.jwbf.mediawiki.actions.login;

import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_15;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_16;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_17;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_18;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_19;
import static net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version.MW1_20;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.actions.util.SupportedBy;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

import org.jdom.Element;

/**
 * 
 * @author Thomas Stock
 */
@Slf4j
@SupportedBy({ MW1_15, MW1_16, MW1_17, MW1_18, MW1_19, MW1_20 })
public class PostLogin extends MWAction {

  private Post msg;

  private final String success = "Success";
  private final String wrongPass = "WrongPass";
  private final String notExists = "NotExists";
  private final String needToken = "NeedToken";
  private LoginData login = null;
  private boolean reTry = false;
  private boolean reTryLimit = true;
  private final String username;
  private final String pw;
  private final String domain;

  /**
   * 
   * @param username
   *          the
   * @param pw
   *          password
   * @param domain
   *          a
   * @param login
   *          a
   */
  public PostLogin(final String username, final String pw, final String domain, LoginData login) {
    super();
    this.login = login;
    this.username = username;
    this.pw = pw;
    this.domain = domain;
    msg = getLoginMsg(username, pw, domain, null);

  }

  private Post getLoginMsg(final String username, final String pw, final String domain,
      final String token) {
    Post pm = new Post(MediaWiki.URL_API + "?action=login&format=xml");
    pm.addParam("lgname", username);
    pm.addParam("lgpassword", pw);
    if (domain != null)
      pm.addParam("lgdomain", domain);
    if (token != null) {
      pm.addParam("lgtoken", token);
    }
    return pm;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String processAllReturningText(final String s) {

    Element root = getRootElement(s);
    findContent(root);

    return s;
  }

  /**
   * @param startElement
   *          the, where the search begins
   */
  private void findContent(final Element startElement) {

    Element loginEl = startElement.getChild("login");
    String result = loginEl.getAttributeValue("result");
    if (result.equalsIgnoreCase(success)) {
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("userId", loginEl.getAttribute("lguserid").toString());
      login.setup(loginEl.getAttributeValue("lgusername"), true);
    } else if (result.equalsIgnoreCase(needToken) && reTryLimit) {
      msg = getLoginMsg(username, pw, domain, loginEl.getAttributeValue("token"));
      reTry = true;
      reTryLimit = false;
    } else if (result.equalsIgnoreCase(wrongPass)) {
      throw new ProcessException("Wrong Password");
    } else if (result.equalsIgnoreCase(notExists)) {
      throw new ActionException("No such User");
    } else if (result.equalsIgnoreCase("Throttled")) {
      throw new ActionException("Throttled");
    }

  }

  /**
   * {@inheritDoc}
   */
  public HttpAction getNextMessage() {
    return msg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.jwbf.mediawiki.actions.util.MWAction#hasMoreMessages()
   */
  @Override
  public boolean hasMoreMessages() {
    boolean temp = super.hasMoreMessages() || reTry;
    reTry = false;
    return temp;
  }

}
