package net.sourceforge.jwbf.mediawiki.actions.queries;
/*
 * Copyright 2007, 2015 Tobias Knerr, Marco Ammon
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
 * Tobias Knerr
 * Marco Ammon
 *
 */

import java.util.Iterator;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.XmlConverter;
import net.sourceforge.jwbf.mapper.XmlElement;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * action class using the MediaWiki-api's "list=allusers"
 *
 * @author Marco Ammon
 * @since MediaWiki 1.11
 */
public class AllUsers extends BaseQuery<String> {

    private static final Logger log = LoggerFactory.getLogger(AllUsers.class);
    private final int limit = 50;
    private final MediaWikiBot bot;
    private final boolean editsOnly;
    private final boolean activeOnly;

    /**
     *
     * @param editsOnly if true, limited to users with at least 1 edit
     * @param activeOnly if true, limited to users with at least 1 contribution
     * in the last 30 days ("active users")
     */
    public AllUsers(final MediaWikiBot bot, final boolean editsOnly, final boolean activeOnly) {
        super(bot);
        this.bot = bot;
        this.editsOnly = editsOnly;
        this.activeOnly = activeOnly;
    }
    
    public AllUsers(final MediaWikiBot bot){
        this(bot, false, false);
    }

    /**
     * gets the information about a follow-up page from a provided api response.
     * If there is one, a new request is added to msgs by calling
     * generateRequest.
     *
     * @param xml text for parsing
     */
    @Override
    protected Optional<String> parseHasMore(final String xml) {
        return parseXmlHasMore(xml, "allusers", "aufrom", "aufrom");
    }

    /**
     * picks the username from a MediaWiki api response.
     *
     * @param s text for parsing
     */
    @Override
    protected ImmutableList<String> parseElements(String s) {
        ImmutableList.Builder<String> titleCollection = ImmutableList.builder();
        Optional<XmlElement> childOpt = XmlConverter.getChildOpt(s, "query", "allusers");
        if (childOpt.isPresent()) {
            for (XmlElement element : childOpt.get().getChildren("u")) {
                titleCollection.add(element.getAttributeValue("name"));
            }
        }
        return titleCollection.build();
    }

    @Override
    protected HttpAction prepareNextRequest() {
        RequestBuilder requestBuilder = new ApiRequestBuilder() //
                .action("query") //
                .formatXml() //
                .param("list", "allusers")
                .param("aulimit", limit);
        if (editsOnly) {
            requestBuilder.param("auwitheditsonly", true);
        }
        if (activeOnly) {
            requestBuilder.param("auactiveusers", true);
        }
        Optional<String> ilcontinue = nextPageInfoOpt();
        if (ilcontinue.isPresent()) {
            requestBuilder.param("aufrom", MediaWiki.urlEncode(ilcontinue.get()));
        }
        return requestBuilder.buildGet();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<String> copy() {
        return new AllUsers(bot, editsOnly, activeOnly);
    }

}
