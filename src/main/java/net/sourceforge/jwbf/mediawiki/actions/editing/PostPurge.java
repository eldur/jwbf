/*
 * Copyright 2016 Marco Ammon <ammon.marco@t-online.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.jwbf.mediawiki.actions.editing;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.actions.util.PermissionException;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action class using the MediaWiki-API  to purge articles in your MediaWiki. add
 * the following line to your MediaWiki's  LocalSettings.php:<br>
 * Purge an article with
 * <pre>
 * String name = ...
 * MediaWikiBot bot = ...
 * Userinfo ui = bot.getUserinfo();
 * bot.performAction(new PostPurge(ui, name);
 * </pre>
 *
 * @author Marco Ammon<ammon.marco@t-online.de>
 * 
 * @see <a href="http://www.mediawiki.org/wiki/API:Purge">"action=purge"</a>
 */

public class PostPurge extends MWAction {

    private Post msg;
    private static final Logger log = LoggerFactory.getLogger(PostPurge.class);
    public static final int MODE_PURGE = 0;
    public static final int MODE_FORCE_LINKUPDATE = 1;
    public static final int MODE_FORCE_RECURSIVE_LINKUPDATE = 2;
    public final int RESULT_PURGED = 0;    
    public final int RESULT_MISSING = 1;    
    public final int RESULT_INVALID = 2;    
    public final int RESULT_UNKNOWN_ERROR = 3;
    
    /** 
    * @param userinfo   The userinfo of the currently logged-in user. Must contain
    *                   "purge" right. 
    * @param titles     ImmutableList&lt;String&gt; of titles to be purged
    * @param mode       Mode:
    *                   * MODE_PURGE: default purge without forced linkupdate.
    *                   * MODE_FORCE_LINKUPDATE: forces linkupdate.
    *                   * MODE_FORCE_RECURSIVE_LINKUPDATE: forces linkupdate
    *                       on any page that transcludes an element of titles.
    * 
    */

    public PostPurge(final Userinfo userinfo, final ImmutableList<String> titles, final int mode) {
        super();
        if (!userinfo.getRights().contains("purge")) {
            throw new PermissionException("The given user doesn't have the rights to purge.");
        }
        RequestBuilder builder = new ApiRequestBuilder()
                .action("purge")
                .param("titles", MediaWiki.urlEncode(MediaWiki.pipeJoined(titles)))
                .param("format", "xml");

        switch (mode) {
            case MODE_PURGE:
                break;
            case MODE_FORCE_LINKUPDATE:
                builder.param("forcelinkupdate", true);
                break;
            case MODE_FORCE_RECURSIVE_LINKUPDATE:
                builder.param("forcerecursivelinkupdate", true);
                break;
            default:
                throw new IllegalArgumentException("Mode has to have a value of either 0, 1 or 2.");
        }
        msg = builder.buildPost();

        System.out.println(msg);
    }
    
    /**
     * 
     * @param userinfo  The userinfo of the currently logged-in user. Must contain
    *                   "purge" right.  
     * @param title     A single page title which should be purged
     */
    public PostPurge(final Userinfo userinfo, final String title) {
      this(userinfo, new String[] {title});
    }
    
    /**
     * 
     * @param userinfo  The userinfo of the currently logged-in user. Must contain
    *                   "purge" right.
    *  @param titles    String[] of titles which should be purged
    */
    public PostPurge(final Userinfo userinfo, final String[] titles) {
        this(userinfo, MediaWiki.nullSafeCopyOf(titles));
    }
    
    /**
     * 
     * @param userinfo  The userinfo of the currently logged-in user. Must contain
    *                   "purge" right.
    *  @param titles    ImmutableList&lt;String&gt; of titles which should be purged
    */
    public PostPurge(final Userinfo userinfo, final ImmutableList<String> titles){
        this (userinfo, titles, 0);
    }
    

    @Override
    public String processReturningText(final String s, HttpAction ha) {
        parseXml(s);
        return "";
    }

    @VisibleForTesting
    void parseXml(String xml) {
        log.debug("Got returning text: \"{}\"", xml);
        XmlElement root = XmlConverter.getRootElement(xml);
        XmlElement purge = root.getChild("purge");
        for (XmlElement page : purge.getChildren("page")) {
            String title = page.getAttributeValue("title");
            if (page.hasAttribute("purged")) {
                logPurge(title, RESULT_PURGED);
            } else if (page.hasAttribute("missing")) {
                logPurge(title, RESULT_MISSING);
            } else if (page.hasAttribute("invalid")) {
                logPurge(title, RESULT_INVALID);
            } else {
                logPurge(title, RESULT_UNKNOWN_ERROR);
            }
        }
    }
    
    @VisibleForTesting
    void logPurge(String title, int result){
        switch(result){
            case RESULT_PURGED:
                log.info(title + "has been successfully purged.");
                break;
            case RESULT_MISSING:
                log.error(title + " is non-existent.");
                break;
            case RESULT_INVALID:
                log.error(title + " is an invalid title.");
                break;
            default:
                log.error(title + " was not purged. Unknown error.");
        }
    }
    
    @Override
    public HttpAction getNextMessage() {
        return msg;
    }

}
