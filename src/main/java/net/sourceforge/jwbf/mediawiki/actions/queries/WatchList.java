package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.WatchListResults;
import net.sourceforge.jwbf.core.contentRep.WatchResponse;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchList extends BaseQuery<WatchResponse> {
	public static final int DEFAULT_NS = 0;

	private static final Logger log = LoggerFactory.getLogger(WatchList.class);

	private int limit = -1;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final ImmutableList<Integer> namespaces;

	private WatchListResults responseList;

	private HashMap<String, List<String>> params;

	public WatchList(MediaWikiBot bot, HashMap<String, List<String>> params,
			int limit, int... namespaces) {
		this(bot, params, limit, Ints.asList(namespaces));
	}

	public WatchList(MediaWikiBot bot, HashMap<String, List<String>> params,
			int limit, List<Integer> namespaces) {
		super(bot);
		if (params != null)
			this.params = params;
		else
			this.params = new HashMap<>();
		this.limit = limit;
		this.namespaces = ImmutableList.copyOf(namespaces);
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public WatchList(MediaWikiBot bot, HashMap<String, List<String>> params) {
		this(bot, params, DEFAULT_NS);
	}

	@Override
	protected WatchList copy() {
		return new WatchList(bot(), params, limit, namespaces);
	}

	@Override
	protected HttpAction prepareNextRequest() {
		RequestBuilder requestBuilder = new ApiRequestBuilder().action("query") //
				.formatJson() //
				.param("continue", MediaWiki.urlEncode("-||")) //
				.param("list", "watchlist");
		if (limit < 1)
			requestBuilder.param("wllimit", "max");
		else
			requestBuilder.param("wllimit", limit);

		for (Entry<String, List<String>> entry : params.entrySet()) {
			String param = "";
			for (String value : entry.getValue()) {
				param += "|" + value;
			}
			if (!param.isEmpty())
				param = param.substring(1);
			requestBuilder.param(entry.getKey(), MediaWiki.urlEncode(param));
		}

		log.debug("using query {}", requestBuilder.build());
		return requestBuilder.buildGet();
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
