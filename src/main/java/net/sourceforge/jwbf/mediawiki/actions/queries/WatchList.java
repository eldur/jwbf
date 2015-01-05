package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.mapper.JsonMapper;
import net.sourceforge.jwbf.mediawiki.ApiRequestBuilder;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO add public if a working test and integration test (IT) is present
class WatchList extends BaseQuery<WatchResponse> {

  public enum WatchListProperties {
    USER("user"), //
    TITLE("title"), //
    COMMENT("comment"), //
    PARSED_COMMENT("parsedcomment"), //
    TIMESTAMP("timestamp"), //
    NOTIFICATION_TIMESTAMP("notificationtimestamp"), //
    IDS("ids"), //
    SIZES("sizes"), //
    PATROL("patrol"), //
    FLAGS("flags");

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

  private static final Joiner PARAM_JOINER = Joiner.on('|');

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
  private final ImmutableList<EditType> editType;
  private final boolean showBots;
  private final boolean showAnonymous;
  private final boolean showMinor;

  private WatchListResults responseList;

  private WatchList(Builder builder) {
    super(builder.bot);
    if (!bot().isLoggedIn()) {
      throw new ActionException("Please login first");
    }
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

  private static String joinParams(List<?> params) {
    return MediaWiki.urlEncode(PARAM_JOINER.join(params));
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
    RequestBuilder requestBuilder = new ApiRequestBuilder().action("query") //
        .formatJson() //
        .param("continue", MediaWiki.urlEncode("-||")) //
        .param("list", "watchlist");
    requestBuilder.param("wlnamespace", MediaWiki.urlEncodedNamespace(namespaces));
    if (!properties.isEmpty()) {
      requestBuilder.param("wlprop", joinParams(Lists.newArrayList(properties)));
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
      requestBuilder.param("wltype", joinParams(Lists.newArrayList(editType)));
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
    return joinParams(shows);
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
    private String user = null;
    private String excludeUser = null;
    private Date start;
    private Date end;
    private Direction dir;

    public Builder(MediaWikiBot bot) {
      this.bot = bot;
    }

    /**
     * How many results to return per request. Do not set it to return the maximum.
     */
    public Builder withLimit(int limit) {
      this.limit = Optional.of(limit);
      return this;
    }

    /**
     * Only list pages in these namespaces
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
      return onlyTypes(ImmutableList.copyOf(types));
    }

    public Builder onlyTypes(ImmutableList<EditType> types) {
      this.editTypes = types;
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
      if (end != null && start != null && end.compareTo(start) < 0) {
        throw new IllegalArgumentException("start must be before end");
      }
      this.start = start;
      return this;
    }

    public Builder withEnd(Date end) {
      if (start != null && end != null && end.compareTo(start) < 0) {
        throw new IllegalArgumentException("end must be later than start");
      }
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
