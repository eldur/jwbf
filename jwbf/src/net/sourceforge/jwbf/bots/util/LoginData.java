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
package net.sourceforge.jwbf.bots.util;

/**
 * TODO API related, use only if posting data works.
 * http://www.mediawiki.org/wiki/API#Posting_Data_.2F_needs_major_editPage.php_rewrite
 * 
 * @author Thomas Stock
 *
 */
public class LoginData {
	
	private final int userid;
	private final String botName;
	private final String loginToken;
	
	public LoginData(int userid, String botName, String loginToken) {
		this.userid = userid;
		this.botName = botName;
		this.loginToken = loginToken;
	}
	
	public String get() {
		return "& lgtoken=123ABC & lgusername="+ botName + " & lguserid=23456";
	}

}
