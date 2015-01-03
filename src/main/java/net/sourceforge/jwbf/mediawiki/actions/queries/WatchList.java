package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.WatchListResults;
import net.sourceforge.jwbf.core.contentRep.WatchResponse;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

public class WatchList extends BaseQuery<WatchResponse> {
	public static final int DEFAULT_NS = 0;
	private static final Joiner PARAM_JOINER = Joiner.on('|');

	private static final Logger log = LoggerFactory.getLogger(WatchList.class);

	private int limit = -1;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final ImmutableList<Integer> namespaces;
	private QueryParameter params;

	private WatchListResults responseList;

	public WatchList(MediaWikiBot bot, int limit, QueryParameter params,
			int... namespaces) {
		this(bot, limit, params, Ints.asList(namespaces));
	}

	public WatchList(MediaWikiBot bot, int limit, QueryParameter params,
			List<Integer> namespaces) {
		super(bot);
		if (!bot.isLoggedIn())
			throw new ActionException("Please login first");
		this.limit = limit;
		this.namespaces = ImmutableList.copyOf(namespaces);
		this.params = params;
		initMapper();
	}

	public WatchList(MediaWikiBot bot) {
		this(bot, -1, null, DEFAULT_NS);
	}

	private void initMapper() {
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	private String joinParam(Set<?> params) {
		return MediaWiki.urlEncode(PARAM_JOINER.join(params));
	}

	private RequestBuilder buildRequest() {
		RequestBuilder request = new ApiRequestBuilder().action("query") //
				.formatJson() //
				.param("continue", MediaWiki.urlEncode("-||")) //
				.param("list", "watchlist");
		request.param("wlnamespace", MediaWiki.urlEncodedNamespace(namespaces));
		if (limit < 1)
			request.param("wllimit", "max");
		else
			request.param("wllimit", limit);
		Iterator<Entry<String, Set<String>>> it = params.iterator();
		while (it.hasNext()) {
			Entry<String, Set<String>> entry = it.next();
			request.param(entry.getKey(), joinParam(entry.getValue()));
		}
		return request;
	}

	@Override
	protected WatchList copy() {
		return new WatchList(bot(), limit, params, namespaces);
	}

	@Override
	protected HttpAction prepareNextRequest() {
		RequestBuilder request = buildRequest();
		if (hasNextPageInfo()) {
			request.param("wlcontinue", MediaWiki.urlEncode(getNextPageInfo()));
		}
		log.debug("using query {}", request.build());
		return request.buildGet();

	}

	@Override
	protected ImmutableList<WatchResponse> parseElements(String json) {
		System.out.println(json);
		try {
			this.responseList = objectMapper.readValue(json,
					WatchListResults.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ImmutableList.copyOf(responseList.getResults());
	}

	@Override
	protected Optional<String> parseHasMore(String s) {
		if (responseList.canContinue()) {
			return Optional.of(responseList.getContinueToken());
		} else {
			return Optional.absent();
		}
	}

}
