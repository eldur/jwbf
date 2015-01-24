package net.sourceforge.jwbf.mediawiki.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.sourceforge.jwbf.core.actions.Post;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.editing.GetApiToken;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;

public class WatchUnwatchAction extends MWAction {

    private static final Logger log = LoggerFactory.getLogger(WatchUnwatchAction.class);

    private boolean watch;
    private ImmutableList<String> titles;
    private GetApiToken watchToken;
    private boolean actionToken = true;
    private boolean hasMore = true;

    public WatchUnwatchAction(boolean watch, String... titles) {
        this.watch = watch;
        this.titles = ImmutableList.copyOf(titles);
        this.watchToken = new GetApiToken(GetApiToken.Intoken.WATCH, titles);
    }

    @Override
    public String processReturningText(String s, HttpAction action) {
        System.out.println(s);
        if (actionToken) {
            watchToken.processReturningText(s, action);
            actionToken = false;
        }
        MediaWiki.checkResponseForError(s);
        return s;
    }

    @Override
    public HttpAction getNextMessage() {
        if (actionToken) {
            return watchToken.popAction();
        }
        hasMore = false;
        RequestBuilder requestBuilder = new ApiRequestBuilder() //
                .action("watch") //
                .formatJson() //
                .postParam(watchToken.get().token()) //
                .param("titles", MediaWiki.urlEncode(MediaWiki.pipeJoined(titles))) //
        ;
        if (!watch) {
            requestBuilder.param("unwatch", !watch);
        }

        Post msg = requestBuilder.buildPost();
        log.debug("watch url: \"{}\"", msg.getRequest());
        return msg;
    }

    @Override
    public boolean hasMoreMessages() {
        return hasMore;
    }
}
