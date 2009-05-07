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
package net.sourceforge.jwbf.actions.mediawiki.editing;

import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_09;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_10;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_11;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_12;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_13;
import static net.sourceforge.jwbf.actions.mediawiki.MediaWiki.Version.MW1_14;

import java.util.Hashtable;

import net.sourceforge.jwbf.actions.Get;
import net.sourceforge.jwbf.actions.Post;
import net.sourceforge.jwbf.actions.mediawiki.MediaWiki;
import net.sourceforge.jwbf.actions.mediawiki.util.MWAction;
import net.sourceforge.jwbf.actions.mediawiki.util.SupportedBy;
import net.sourceforge.jwbf.actions.mediawiki.util.VersionException;
import net.sourceforge.jwbf.actions.util.ActionException;
import net.sourceforge.jwbf.actions.util.HttpAction;
import net.sourceforge.jwbf.actions.util.ProcessException;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.bots.util.JwbfException;
import net.sourceforge.jwbf.contentRep.ContentAccessable;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * Writes an article.
 * 
 * 
 * @author Thomas Stock
 */
@SupportedBy({MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14})
public class PostModifyContent extends MWAction {

	private boolean first = true;
	private boolean second = true;
	
	private final ContentAccessable a;
	private final Logger log = Logger.getLogger(PostModifyContent.class);
	private Hashtable<String, String> tab = new Hashtable<String, String>();
	private MediaWikiBot bot;
	private GetApiToken apiReq = null;
	private HttpAction apiGet = null;
	private HttpAction initOldGet = null;
	private Post postModify = null;
	private boolean apiEdit = false;
	/**
	 * 
	 * @param a the
	 * @throws ProcessException a
	 * @throws ActionException a
	 * @throws VersionException a
	 */
	public PostModifyContent(MediaWikiBot bot, final ContentAccessable a) throws VersionException, ActionException, ProcessException {
		super(bot.getVersion());
		this.a = a;
		this.bot = bot;
		

	}


	public HttpAction getNextMessage() {

		if (first) {
			try {
				if (!bot.isEditApi()) 
					throw new VersionException("write api off - user triggerd");
				switch (bot.getSiteinfo().getVersion()) {
				case MW1_09:
				case MW1_10:
				case MW1_11:
				case MW1_12:
					throw new VersionException("write api not avalibal");
				default:
					break;
				}
				first = false;
				if (!(bot.getUserinfo().getRights().contains("edit") 
						&& bot.getUserinfo().getRights().contains("writeapi")  )) {
					throw new VersionException("write api not avalibal");
				}
				apiReq = new GetApiToken(GetApiToken.Intoken.EDIT,
						a.getLabel(), bot.getSiteinfo(), bot.getUserinfo());
				apiGet = apiReq.getNextMessage();
				apiEdit = true;
				return apiGet;

			} catch (VersionException e) {
				String uS = "/index.php?title="
						+ MediaWiki.encode(a.getLabel())
						+ "&action=edit&dontcountme=s";
				initOldGet = new Get(uS);
				first = false;
				return initOldGet;
			} catch (JwbfException e) {
				e.printStackTrace();
			}
		}
		if (apiEdit) {
			String uS = "/api.php?action=edit&title=" + MediaWiki.encode(a.getLabel());
			postModify = new Post(uS);
			postModify.addParam("summary", a.getEditSummary());
			postModify.addParam("text", a.getText());
//			postModify.addParam("watch", "unknown") // TODO add or rm
			if(a.isMinorEdit())
				postModify.addParam("minor", "");
			else 
				postModify.addParam("notminor", "");
			postModify.addParam("token", apiReq.getToken());
			
//			&section=new&watch&basetimestamp=2008-03-20T17:26:39Z&
		} else {
			String uS = "/index.php?title=" + MediaWiki.encode(a.getLabel())
					+ "&action=submit";

			postModify = new Post(uS);
			postModify.addParam("wpSave", "Save");

			postModify.addParam("wpStarttime", tab.get("wpStarttime"));

			postModify.addParam("wpEditToken", tab.get("wpEditToken"));

			postModify.addParam("wpEdittime", tab.get("wpEdittime"));

			postModify.addParam("wpTextbox1", a.getText());

			String editSummaryText = a.getEditSummary();
			if (editSummaryText != null && editSummaryText.length() > 200) {
				editSummaryText = editSummaryText.substring(0, 200);
			}

			postModify.addParam("wpSummary", editSummaryText);
			if (a.isMinorEdit()) {

				postModify.addParam("wpMinoredit", "1");

			}

			log.info("WRITE: " + a.getLabel());

			
		}
		second = false;

		return postModify;
	}

	@Override
	public boolean hasMoreMessages() {
		return first || second;
	}

	@Override
	public String processReturningText(String s, HttpAction hm)
			throws ProcessException {
		if (s.contains("error")) { // FIXME RM
//			if (s.contains("edit") && bot.getVersion() == Version.MW1_13) { // FIXME invalid
//				throw new ProcessException("please check if $wgEnableWriteAPI = true;");
//			}
			throw new ProcessException(s.substring(0, 700));
		}
		if (initOldGet != null && hm.getRequest().equals(initOldGet.getRequest())) {
			getWpValues(s, tab);
			log.debug(tab);
		} else if (apiGet != null && hm.getRequest().equals(apiGet.getRequest())) {
			log.debug("parseapi"); //TODO RM
			apiReq.processReturningText(s, hm);
		} 
//		else {
//			log.debug(s); //TODO RM
//		}
		
		return s;
	}

	/**
	 * 
	 * @param text
	 *            where to search
	 * @param tab
	 *            tabel with required values
	 */
	private void getWpValues(final String text, Hashtable<String, String> tab) {

		String[] tParts = text.split("\n");
		// System.out.println(tParts.length);
		for (int i = 0; i < tParts.length; i++) {
			if (tParts[i].indexOf("wpEditToken") > 0) {
				// \<input type='hidden' value=\"(.*?)\" name=\"wpEditToken\"
				int begin = tParts[i].indexOf("value") + 7;
				int end = tParts[i].indexOf("name") - 2;
				// System.out.println(line.substring(begin, end));
				// System.out.println("read wp token:" + tParts[i]);
				tab.put("wpEditToken", tParts[i].substring(begin, end));

			} else if (tParts[i].indexOf("wpEdittime") > 0) {
				// value="(\d+)" name=["\']wpEdittime["\']
				int begin = tParts[i].indexOf("value") + 7;
				int end = tParts[i].indexOf("name") - 2;
				// System.out.println( "read wp edit: " +
				// tParts[i].substring(begin, end));

				tab.put("wpEdittime", tParts[i].substring(begin, end));

			} else if (tParts[i].indexOf("wpStarttime") > 0) {
				// value="(\d+)" name=["\']wpStarttime["\']
				int begin = tParts[i].indexOf("value") + 7;
				int end = tParts[i].indexOf("name") - 2;
				// System.out.println("read wp start:" + tParts[i]);

				tab.put("wpStarttime", tParts[i].substring(begin, end));

			}
		}

	}


}
