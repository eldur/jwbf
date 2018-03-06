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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.PermissionException;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.contentRep.LoginData;

/** @author Thomas Stock */
public class PostLogin extends MWAction {

  private static final Logger log = LoggerFactory.getLogger(PostLogin.class);

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
   * @param username the
   * @param pw password
   * @param domain a
   */
  public PostLogin(final String username, final String pw, final String domain) {
    this.login = new LoginData();
    this.username = username;
    this.pw = pw;
    this.domain = domain;
    msg = getLoginMsg(username, pw, domain, null);
  }

  private Post getLoginMsg(
      final String username, final String pw, final String domain, final String token) {
    RequestBuilder loginRequest =
        new ApiRequestBuilder() //
            .action("login") //
            .formatXml() //
            .postParam("lgname", username) //
            .postParam("lgpassword", pw);
    if (domain != null) {
      loginRequest.postParam("lgdomain", domain);
    }
    if (token != null) {
      loginRequest.postParam("lgtoken", token);
    }
    return loginRequest.buildPost();
  }

  /** {@inheritDoc} */
  @Override
  public String processAllReturningText(final String s) {
    XmlElement root = XmlConverter.getRootElement(s);
    findContent(root);

    return s;
  }

  /** @param startXmlElement the, where the search begins */
  private void findContent(final XmlElement startXmlElement) {

    String result = startXmlElement.getChildAttributeValue("login", "result");
    if (result.equalsIgnoreCase(success)) {
      login.setup(startXmlElement.getChildAttributeValue("login", "lgusername"), true);
    } else if (result.equalsIgnoreCase(needToken) && reTryLimit) {
      msg =
          getLoginMsg(
              username, pw, domain, startXmlElement.getChildAttributeValue("login", "token"));
      reTry = true;
      reTryLimit = false;
    } else if (result.equalsIgnoreCase(wrongPass)) {
      throw new PermissionException("Wrong Password");
    } else if (result.equalsIgnoreCase(notExists)) {
      throw new ActionException("No such User");
    } else if (result.equalsIgnoreCase("Throttled")) {
      throw new ActionException("Throttled");
    }
  }

  /** {@inheritDoc} */
  @Override
  public HttpAction getNextMessage() {
    return msg;
  }

  @Override
  public boolean hasMoreMessages() {
    boolean temp = super.hasMoreMessages() || reTry;
    reTry = false;
    return temp;
  }

  public LoginData getLoginData() {
    return login;
  }
}
