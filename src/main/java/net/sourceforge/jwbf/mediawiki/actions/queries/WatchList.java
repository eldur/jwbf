package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

	public enum WatchListProperties {
		//TODO: add all the properties
		USER("user"), TITLE("title"), COMMENT("comment"), TIMESTAMP("timestamp"), PARSED_COMMENT(
				"parsedcomment");

		private String name;

		private WatchListProperties(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public static final int DEFAULT_NS = 0;
	private static final Joiner PARAM_JOINER = Joiner.on('|');

	private static final Logger log = LoggerFactory.getLogger(WatchList.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final int limit;
	private final ImmutableList<Integer> namespaces;
	private WatchListProperties[] properties;
	private boolean showBots;
	private boolean showAnonymous;
	private boolean showMinor;

	private WatchListResults responseList;

	private WatchList(Builder builder) {
		super(builder.getBot());
		if (!bot().isLoggedIn())
			throw new ActionException("Please login first");
		this.limit = builder.getLimit();
		this.namespaces = ImmutableList.copyOf(Ints.asList(builder
				.getNamespaces()));
		this.properties = builder.getProperties();
		this.showBots = builder.showBots;
		this.showAnonymous = builder.showAnonymous;
		this.showMinor = builder.showMinor;

		initMapper();
	}

	private void initMapper() {
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	private String joinParam(Set<?> params) {
		return MediaWiki.urlEncode(PARAM_JOINER.join(params));
	}

	@Override
	protected WatchList copy() {
		return new WatchList(bot());
	}

	@Override
	protected HttpAction prepareNextRequest() {
		RequestBuilder requestBuilder = new ApiRequestBuilder().action("query") //
				.formatJson() //
				.param("continue", MediaWiki.urlEncode("-||")) //
				.param("list", "watchlist");
		requestBuilder.param("wlnamespace",
				MediaWiki.urlEncodedNamespace(namespaces)).param(
				"wlprop",
				joinParam(new HashSet<WatchListProperties>(Arrays
						.asList(properties))));
		if (showBots)
			requestBuilder.param("wlshow", "bot");
		else
			requestBuilder.param("wlshow", "!bot");
		if (showAnonymous)
			requestBuilder.param("wlshow", "anon");
		else
			requestBuilder.param("wlshow", "!anon");
		if (showMinor)
			requestBuilder.param("wlshow", "minor");
		else
			requestBuilder.param("wlshow", "!minor");
		if (limit < 1)
			requestBuilder.param("wllimit", "max");
		else
			requestBuilder.param("wllimit", limit);
		if (hasNextPageInfo()) {
			requestBuilder.param("wlcontinue", getNextPageInfo());
		}
		log.debug("using query {}", requestBuilder.build());
		return requestBuilder.buildGet();

	}

	@Override
	protected ImmutableList<WatchResponse> parseElements(String json) {
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

	public static Builder from(MediaWikiBot bot) {
		return new Builder(bot);
	}

	public static class Builder {

		private MediaWikiBot bot;
		private int limit = -1;
		private WatchListProperties[] properties;
		private boolean showBots = true;
		private boolean showAnonymous = true;
		private int[] namespaces = new int[] { DEFAULT_NS };
		private boolean showMinor;

		public Builder(MediaWikiBot bot) {
			this.bot = bot;
		}
		/**
		 * How many results to return per request.
		 * Use a negative value to return the maximum.
		 * @param limit
		 * @return
		 */
		public Builder withLimit(int limit) {
			this.limit = limit;
			return this;
		}

		/**
		 * Only list pages in these namespaces
		 * @param namespaces
		 * @return
		 */
		public Builder withNamespaces(int... namespaces) {
			this.namespaces = namespaces;
			return this;
		}

		public Builder withProperties(WatchListProperties... properties) {
			this.properties = properties;
			return this;
		}

		public Builder showBots(boolean show) {
			this.showBots = show;
			return this;
		}

		public Builder showAnonymous(boolean show) {
			this.showAnonymous = show;
			return this;
		}

		public Builder showMinor(boolean show) {
			this.showMinor = show;
			return this;
		}

		public WatchList build() {
			return new WatchList(this);
		}

		public int[] getNamespaces() {
			return namespaces;
		}

		public MediaWikiBot getBot() {
			return bot;
		}

		public int getLimit() {
			return limit;
		}

		public WatchListProperties[] getProperties() {
			return properties;
		}

		public boolean showBots() {
			return showBots;
		}

		public boolean showAnonymous() {
			return showAnonymous;
		}

		public boolean showMinor() {
			return showMinor;
		}
	}

}
