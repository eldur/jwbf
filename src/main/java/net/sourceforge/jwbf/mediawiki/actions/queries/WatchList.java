package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

public class WatchList extends BaseQuery<WatchResponse> {

	public enum WatchListProperties {
		USER("user"), TITLE("title"), COMMENT("comment"), PARSED_COMMENT(
				"parsedcomment"), TIMESTAMP("timestamp"), NOTIFICATION_TIMESTAMP(
				"notificationtimestamp"), IDS("ids"), SIZES("sizes"), PATROL(
				"patrol"), FLAGS("flags");

		private String name;

		private WatchListProperties(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public enum EditType {
		EDIT("edit"), EXTERNAL("external"), NEW("new"), LOG("log");

		private String name;

		private EditType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public enum Direction {
		OLDER("older"), NEWER("newer");

		private String name;

		private Direction(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	public static final int DEFAULT_NS = 0;
	private static final Joiner PARAM_JOINER = Joiner.on('|');

	private static final Logger log = LoggerFactory.getLogger(WatchList.class);

	private final JsonMapper mapper = new JsonMapper();

	private final int limit;
	private final Date start;
	private final Date end;
	private final Direction dir;
	private final ImmutableList<Integer> namespaces;
	private final ImmutableList<WatchListProperties> properties;
	private final String user;
	private final String excludeUser;
	private final ImmutableList<EditType> editType;
	private final boolean showBots;
	private final boolean showAnonymous;
	private final boolean showMinor;

	private WatchListResults responseList;

	private WatchList(Builder builder) {
		super(builder.bot);
		if (!bot().isLoggedIn())
			throw new ActionException("Please login first");
		this.limit = builder.limit;
		this.start = builder.start;
		this.end = builder.end;
		this.dir = builder.dir;
		this.namespaces = builder.namespaces;
		this.properties = builder.properties;
		this.user = builder.user;
		this.excludeUser = builder.excludeUser;
		this.editType = builder.editTypes;
		this.showBots = builder.showBots;
		this.showAnonymous = builder.showAnonymous;
		this.showMinor = builder.showMinor;
	}

	// TODO: do we need to check if it's logged?
	private WatchList(MediaWikiBot bot, int limit, Date start, Date end,
			Direction dir, ImmutableList<Integer> namespaces,
			ImmutableList<WatchListProperties> properties, String user,
			String excludeUser, ImmutableList<EditType> editTypes,
			boolean showBots, boolean showAnonymous, boolean showMinor) {
		super(bot);
		this.limit = limit;
		this.namespaces = namespaces;
		this.start = start;
		this.end = end;
		this.dir = dir;
		this.properties = properties;
		this.user = user;
		this.excludeUser = excludeUser;
		this.editType = editTypes;
		this.showBots = showBots;
		this.showAnonymous = showAnonymous;
		this.showMinor = showMinor;
	}

	// TODO: shouldn't be a static method in MediaWiki?
	private String joinParam(Set<?> params) {
		return MediaWiki.urlEncode(PARAM_JOINER.join(params));
	}

	@Override
	protected WatchList copy() {
		return new WatchList(bot(), limit, start, end, dir, namespaces,
				properties, user, excludeUser, editType, showBots,
				showAnonymous, showMinor);
	}

	@Override
	protected HttpAction prepareNextRequest() {
		RequestBuilder requestBuilder = new ApiRequestBuilder().action("query") //
				.formatJson() //
				.param("continue", MediaWiki.urlEncode("-||")) //
				.param("list", "watchlist");
		requestBuilder.param("wlnamespace",
				MediaWiki.urlEncodedNamespace(namespaces));
		if (!properties.isEmpty())
			requestBuilder.param("wlprop",
					joinParam(new HashSet<WatchListProperties>(properties)));
		if (start != null)
			requestBuilder.param("wlstart", start);
		if (end != null)
			requestBuilder.param("wlend", end);
		if (dir != null)
			requestBuilder.param("wldir", dir.name());
		if (user != null)
			requestBuilder.param("wluser", user);
		else {
			if (excludeUser != null)
				requestBuilder.param("wlexcludeuser", excludeUser);
		}
		if (!editType.isEmpty())
			requestBuilder.param("wltype", joinParam(new HashSet<EditType>(
					editType)));
		Set<String> shows = new HashSet<String>();
		if (showBots)
			shows.add("bot");
		else
			shows.add("!bot");
		if (showAnonymous)
			shows.add("anon");
		else
			shows.add("!anon");
		if (showMinor)
			shows.add("minor");
		else
			shows.add("!minor");
		requestBuilder.param("wlshow", joinParam(shows));
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
		this.responseList = mapper.get(json, WatchListResults.class);
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
		private ImmutableList<WatchListProperties> properties = new ImmutableList.Builder<WatchListProperties>()
				.build();
		private boolean showBots = true;
		private boolean showAnonymous = true;
		private ImmutableList<Integer> namespaces = ImmutableList
				.copyOf(MediaWiki.NS_EVERY);
		private boolean showMinor = true;
		private ImmutableList<EditType> editTypes = new ImmutableList.Builder<EditType>()
				.build();;
		private String user = null;
		private String excludeUser = null;
		private Date start;
		private Date end;
		private Direction dir;

		public Builder(MediaWikiBot bot) {
			this.bot = bot;
		}

		/**
		 * How many results to return per request. Use a negative value to
		 * return the maximum.
		 *
		 * @param limit
		 * @return
		 */
		public Builder withLimit(int limit) {
			this.limit = limit;
			return this;
		}

		/**
		 * Only list pages in these namespaces
		 *
		 * @param namespaces
		 * @return
		 */
		public Builder withNamespaces(int... namespaces) {
			this.namespaces = ImmutableList.copyOf(Ints.asList(namespaces));
			return this;
		}

		public Builder withProperties(WatchListProperties... properties) {
			this.properties = ImmutableList.copyOf(properties);
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

		public Builder onlyTypes(EditType... types) {
			this.editTypes = ImmutableList.copyOf(types);
			return this;
		}

		public Builder onlyUser(String user) {
			this.user = user;
			return this;
		}

		public Builder excludeUser(String user) {
			this.excludeUser = user;
			return this;
		}

		public Builder withStart(Date start) {
			if (end != null && start != null && end.compareTo(start) < 0)
				throw new IllegalArgumentException("start must be before end");
			this.start = start;
			return this;
		}

		public Builder withEnd(Date end) {
			if (start != null && end != null && end.compareTo(start) < 0)
				throw new IllegalArgumentException(
						"end must be later than start");
			this.end = end;
			return this;
		}

		public Builder withDir(Direction dir) {
			this.dir = dir;
			return this;
		}

		public WatchList build() {
			return new WatchList(this);
		}
	}

}
