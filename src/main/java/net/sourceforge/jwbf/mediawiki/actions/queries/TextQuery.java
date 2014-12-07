package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.jwbf.core.actions.RequestBuilder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.contentRep.SearchResult;
import net.sourceforge.jwbf.core.contentRep.SearchResultList;
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

public class TextQuery extends BaseQuery<SearchResult> {
    private static final Joiner PARAM_JOINER = Joiner.on('|');

    public static final int DEFAULT_NS = 0;

    /** What metadata to return */
    public enum SearchInfo {
        totalhits, suggestion;

        public static final Set<SearchInfo> DEFAULT = EnumSet.of(totalhits, suggestion);
    }

    /** Which type of search to perform. */
    public enum SearchWhat {
        /** Search in page titles */
        title,
        /** Search in page text */
        text,
        /** Search for the exact title */
        nearmatch;

        public static final Set<SearchWhat> DEFAULT = EnumSet.of(title);
    }

    public enum SearchProps {
        /** size of the page in bytes. */
        size,
        /** word count of the page. */
        wordcount,
        /** timestamp of when the page was last edited. */
        timestamp,
        /** parsed snippet of the page. */
        snippet,
        /** parsed snippet of the page title. */
        titlesnippet,
        /** parsed snippet of the redirect title. */
        redirectsnippet,
        /** title of the matching redirect. */
        redirecttitle,
        /** parsed snippet of the matching section title. */
        sectionsnippet,
        /** title of the matching section. */
        sectiontitle, title, ;

        public static final Set<SearchProps> DEFAULT = EnumSet.of(size, wordcount, timestamp,
            snippet);
    }

    private static final Logger log = LoggerFactory.getLogger(TextQuery.class);

    /**
     * Constant value for the srlimit-parameter.
     */
    private static final int LIMIT = 50;
    private final JsonMapper mapper = new JsonMapper();
    private final String query;

    private final Set<SearchWhat> what;

    private final Set<SearchInfo> searchInfo;

    private final Set<SearchProps> props;

    private final ImmutableList<Integer> namespaces;

    private SearchResultList resultList;

    /**
     * Create a search request
     *
     * @param query
     *            Search for all page titles (or content) that have this value.
     * @param what
     *            Which type of search to perform.
     * @param searchInfo
     *            Which metadata to return.
     * @param props
     *            Which properties to return.
     * @param namespaces
     *            Search only within these namespaces.
     */
    public TextQuery(MediaWikiBot bot, String query, Set<SearchWhat> what,
            Set<SearchInfo> searchInfo, Set<SearchProps> props, int... namespaces) {
        this(bot, query, what, searchInfo, props, Ints.asList(namespaces));
    }

    public TextQuery(MediaWikiBot bot, String query, Set<SearchWhat> what,
            Set<SearchInfo> searchInfo, Set<SearchProps> props, List<Integer> namespaces) {
        super(bot);
        this.query = query;
        this.what = what;
        this.searchInfo = searchInfo;
        this.props = props;
        this.namespaces = ImmutableList.copyOf(namespaces);
    }

    public TextQuery(MediaWikiBot bot, String query) {
        this(bot, query, SearchWhat.DEFAULT, SearchInfo.DEFAULT, SearchProps.DEFAULT, DEFAULT_NS);
    }

    @Override
    protected TextQuery copy() {
        return new TextQuery(bot(), query, what, searchInfo, props, namespaces);
    }

    @Override
    protected HttpAction prepareNextRequest() {
        RequestBuilder requestBuilder = new ApiRequestBuilder().action("query")
            .formatJson()
            .param("continue", MediaWiki.urlEncode("-||"))
            .param("list", "search")
            .param("srsearch", MediaWiki.urlEncode(query))
            .param("srnamespace", MediaWiki.urlEncodedNamespace(namespaces))
            .param("srwhat", joinParam(what))
            .param("srinfo", joinParam(searchInfo))
            .param("srprop", joinParam(props))
            .param("srlimit", LIMIT);

        if (hasNextPageInfo()) {
            requestBuilder.param("sroffset", getNextPageInfo());
        }

        log.debug("using query {}", requestBuilder.build());
        return requestBuilder.buildGet();
    }

    private String joinParam(Set<? extends Enum<?>> params) {
        return MediaWiki.urlEncode(PARAM_JOINER.join(params));
    }

    @Override
    protected ImmutableList<SearchResult> parseElements(String json) {
        this.resultList = mapper.get(json, SearchResultList.class);
        return ImmutableList.copyOf(resultList.getResults());
    }

    @Override
    protected Optional<String> parseHasMore(String s) {
        if (resultList.canContinue()) {
            return Optional.of(Integer.toString(resultList.getOffset()));
        } else {
            return Optional.absent();
        }
    }

    public int getTotalHits() {
        return resultList.getTotalHits();
    }

    public String getSuggestion() {
        return resultList.getSuggestion();
    }

}
