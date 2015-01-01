package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
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
	private RequestBuilder request;

	private WatchListResults responseList;

	public WatchList(MediaWikiBot bot, int limit, int... namespaces) {
		this(bot, limit, Ints.asList(namespaces));
	}

	public WatchList(MediaWikiBot bot, int limit, List<Integer> namespaces) {
		super(bot);
		this.limit = limit;
		this.namespaces = ImmutableList.copyOf(namespaces);
		buildRequest();
		initMapper();
	}

	public WatchList(MediaWikiBot bot) {
		this(bot, -1, DEFAULT_NS);
	}

	private void initMapper() {
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	public void addParam(String key, String... values) {
		Set<String> params = new HashSet<>();
		params.addAll(Arrays.asList(values));
		request.param(key, joinParam(params));
		System.out.println(request.build());
	}

	private String joinParam(Set<?> params) {
		return MediaWiki.urlEncode(PARAM_JOINER.join(params));
	}

	private void buildRequest() {
		request = new ApiRequestBuilder().action("query") //
				.formatJson() //
				.param("continue", MediaWiki.urlEncode("-||")) //
				.param("list", "watchlist");
		request.param("wlnamespace", MediaWiki.urlEncodedNamespace(namespaces));
		if (limit < 1)
			request.param("wllimit", "max");
		else
			request.param("wllimit", limit);

		log.debug("using query {}", request.build());
		System.out.println(request.build());
	}

	@Override
	protected WatchList copy() {
		return this;
	}

	@Override
	protected HttpAction prepareNextRequest() {
		if (hasNextPageInfo()) {
			request.param("wlcontinue", MediaWiki.urlEncode(getNextPageInfo()));
			System.out.println(getNextPageInfo());
		}
		System.out.println(request.build());
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
