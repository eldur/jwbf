package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.TimeConverter;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

/**
 * A query class to get a list of pages on the current user's watchlist that were changed within the
 * given time period.
 *
 * <p>You must be logged in or pass a user name and his watchlist token to use this class.
 *
 * <p>See also: <a href="https://www.mediawiki.org/wiki/API:Watchlist">API:Watchlist</a>
 *
 * @author Rabah Meradi.
 */
// TODO add public if a working test and integration test (IT) is present
class WatchList extends BaseQuery<WatchResponse> {

  static String formatDate(Date date) {
    return TimeConverter.valueOf(date);
  }

  /** The properties that could be returned with WatchList request. */
  public enum WatchListProperties {
    /** The user who made the change */
    USER("user"),
    /** The title of page */
    TITLE("title"),
    /** The edit/log comment */
    COMMENT("comment"),
    /** The edit/log comment in HTML format */
    PARSED_COMMENT("parsedcomment"),
    /** The time and date of the change */
    TIMESTAMP("timestamp"),
    /** Adds timestamp of when the user was last notified about the edit */
    NOTIFICATION_TIMESTAMP("notificationtimestamp"),
    /** The page ID and revision ID */
    IDS("ids"),
    /** The page size before and after the change */
    SIZES("sizes"),
    /** Whether the change is patrolled. */
    PATROL("patrol"),
    /** The flags associated with the change (bot edit, minor edit ...) */
    FLAGS("flags");
    /** The name of the property like it's used by MW */
    private String name;

    private WatchListProperties(String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }

  public enum EditType {
    /** Regular page edits */
    EDIT("edit"),
    /** External edits */
    EXTERNAL("external"),
    /** Pages creation */
    NEW("new"),
    /** Log entries */
    LOG("log");
    /** The name of the property like it's used by MW */
    private String name;

    private EditType(String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }

  public enum Direction {
    /** Older edits first */
    OLDER("older"),
    /** Newer edits first */
    NEWER("newer");
    /** the name used by MW */
    private String name;

    private Direction(String name) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }

  private static final Logger log = LoggerFactory.getLogger(WatchList.class);

  private final JsonMapper mapper = new JsonMapper();

  private final Optional<Integer> limit;
  private final Date start;
  private final Date end;
  private final Direction dir;
  private final ImmutableList<Integer> namespaces;
  private final ImmutableList<WatchListProperties> properties;
  private final String user;
  private final String excludeUser;
  private final String owner;
  private final String token;

  private final ImmutableList<EditType> editType;
  private final boolean showBots;
  private final boolean showAnonymous;
  private final boolean showMinor;

  private WatchListResults responseList;

  private WatchList(Builder builder) {
    super(builder.bot);
    if (!bot().isLoggedIn()) {
      if (builder.owner == null || builder.token == null) {
        throw new ActionException("Please login first or set owner and token");
      }
    }
    this.limit = builder.limit;
    this.start = builder.start;
    this.end = builder.end;
    this.dir = builder.dir;
    this.namespaces = builder.namespaces;
    this.properties = builder.properties;
    this.user = builder.user;
    this.excludeUser = builder.excludeUser;
    this.owner = builder.owner;
    this.token = builder.token;

    this.editType = builder.editTypes;
    this.showBots = builder.showBots;
    this.showAnonymous = builder.showAnonymous;
    this.showMinor = builder.showMinor;
  }

  private static String joinedAndEncodedParams(List<?> params) {
    return MediaWiki.urlEncode(MediaWiki.pipeJoiner().join(params));
  }

  @Override
  protected WatchList copy() {
    return new WatchList(builderOf(this));
  }

  private WatchList.Builder builderOf(WatchList watchList) {
    return WatchList.from(bot()) //
        .excludeUser(watchList.excludeUser) //
        .onlyTypes(watchList.editType) //
    // TODO complete this self builder
    ;
  }

  @Override
  protected HttpAction prepareNextRequest() {
    RequestBuilder requestBuilder =
        new ApiRequestBuilder()
            .action("query") //
            .formatJson() //
            .param("continue", MediaWiki.urlEncode("-||")) //
            .param("list", "watchlist");
    requestBuilder.param("wlnamespace", MediaWiki.urlEncodedNamespace(namespaces));
    if (!properties.isEmpty()) {
      requestBuilder.param("wlprop", joinedAndEncodedParams(Lists.newArrayList(properties)));
    }
    if (start != null) {
      requestBuilder.param("wlstart", start.toString()); // TODO do formatting in this class
    }
    if (end != null) {
      requestBuilder.param("wlend", end.toString()); // TODO do formatting in this class
    }
    if (dir != null) {
      requestBuilder.param("wldir", dir.toString());
    }
    if (user != null) {
      requestBuilder.param("wluser", user);
    } else {
      if (excludeUser != null) {
        requestBuilder.param("wlexcludeuser", excludeUser);
      }
    }
    if (!editType.isEmpty()) {
      requestBuilder.param("wltype", joinedAndEncodedParams(Lists.newArrayList(editType)));
    }
    requestBuilder.param("wlshow", createShowParamValue());

    if (limit.isPresent()) {
      requestBuilder.param("wllimit", limit.get());
    } else {
      requestBuilder.param("wllimit", "max");
    }
    if (hasNextPageInfo()) {
      requestBuilder.param("wlcontinue", getNextPageInfo());
    }
    log.debug("using query {}", requestBuilder.build());
    return requestBuilder.buildGet();
  }

  private String createShowParamValue() {
    List<String> shows = Lists.newArrayList();
    if (showBots) {
      shows.add("bot");
    } else {
      shows.add("!bot");
    }
    if (showAnonymous) {
      shows.add("anon");
    } else {
      shows.add("!anon");
    }
    if (showMinor) {
      shows.add("minor");
    } else {
      shows.add("!minor");
    }
    return joinedAndEncodedParams(shows);
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
    private Optional<Integer> limit = Optional.absent();
    private ImmutableList<WatchListProperties> properties = ImmutableList.of();
    private boolean showBots = true;
    private boolean showAnonymous = true;
    private ImmutableList<Integer> namespaces = MediaWiki.NS_EVERY;
    private boolean showMinor = true;
    private ImmutableList<EditType> editTypes = ImmutableList.of();
    private String user;
    private String excludeUser;
    private Date start;
    private Date end;
    private Direction dir;
    private String owner;
    private String token;

    public Builder(MediaWikiBot bot) {
      this.bot = bot;
    }

    /** How many results to return per request. Do not set it to return the maximum. */
    public Builder withLimit(int limit) {
      this.limit = Optional.of(limit);
      return this;
    }

    /** Only list pages in these namespaces */
    public Builder withNamespaces(int... namespaces) {
      this.namespaces = ImmutableList.copyOf(Ints.asList(namespaces));
      return this;
    }

    /** Which properties to get. */
    public Builder withProperties(WatchListProperties... properties) {
      this.properties = ImmutableList.copyOf(properties);
      return this;
    }

    /**
     * Whatever to include edits made by bots or not
     *
     * @param show include edits made by bots or not
     */
    public Builder showBots(boolean show) {
      this.showBots = show;
      return this;
    }

    /**
     * Whatever to include edits made by anonymous users or not
     *
     * @param show include edits made by anonymous users or not
     */
    public Builder showAnonymous(boolean show) {
      this.showAnonymous = show;
      return this;
    }

    /**
     * Include or not edits that are marked as minor
     *
     * @param show include minor edits or not
     */
    public Builder showMinor(boolean show) {
      this.showMinor = show;
      return this;
    }

    /**
     * Only list certain types of changes
     *
     * @param types the types of changes that will be listed
     */
    public Builder onlyTypes(EditType... types) {
      return onlyTypes(ImmutableList.copyOf(types));
    }

    public Builder onlyTypes(ImmutableList<EditType> types) {
      this.editTypes = types;
      return this;
    }

    /**
     * Only list changes made by this a specific user
     *
     * @param user the user
     */
    public Builder onlyUser(String user) {
      this.user = user;
      return this;
    }

    /**
     * Exclude changes made by a certain user
     *
     * @param user the user
     */
    public Builder excludeUser(String user) {
      this.excludeUser = user;
      return this;
    }

    /**
     * The user whose watchlist you want
     *
     * @param ownerUser the user
     * @param token the wtahclist token of the user
     */
    public Builder owner(String ownerUser, String token) {
      if (owner != null && token == null) {
        throw new IllegalArgumentException("owner and token mustn't be null");
      }
      this.owner = ownerUser;
      this.token = token;
      return this;
    }

    /**
     * The timestamp to start listing from
     *
     * @param start the timestamp
     */
    public Builder withStart(Date start) {
      if (end != null && start != null && end.compareTo(start) < 0) {
        throw new IllegalArgumentException("start must be before end");
      }
      this.start = start;
      return this;
    }

    /**
     * The timestamp to end listing at
     *
     * @param end the timestamp
     */
    public Builder withEnd(Date end) {
      if (start != null && end != null && end.compareTo(start) < 0) {
        throw new IllegalArgumentException("end must be later than start");
      }
      this.end = end;
      return this;
    }

    /**
     * Direction to list in
     *
     * @param dir the direction
     */
    public Builder withDir(Direction dir) {
      this.dir = dir;
      return this;
    }

    /**
     * Create a WatchList query with the given configuration
     *
     * @return the watchlist query
     */
    public WatchList build() {
      return new WatchList(this);
    }
  }
}
