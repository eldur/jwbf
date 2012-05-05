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
package net.sourceforge.jwbf.mediawiki.contentRep;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO API related, use only if posting data works.
 * http://www.mediawiki.org/wiki/API#Posting_Data_.2F_needs_major_editPage.php_rewrite
 *
 * @author Thomas Stock
 * FIXME check usage
 *
 */
public class LoginData {

	private String userName;
  private final Map<String, String> properties = new HashMap<String, String>();
	private boolean isLoggedIn;

	public LoginData() {
		this.userName = "";
		this.isLoggedIn = false;
	}

	public void setup(String userName, boolean isLoggedIn) {
	  setup(userName, isLoggedIn, null);
	}

	public void setup(String userName, boolean isLoggedIn, Map<String, String> properties) {
		this.userName = userName;
		this.isLoggedIn = isLoggedIn;
		if (properties != null)
		  this.properties.putAll(properties);
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public String getUserName() {
		return userName;
	}

}
