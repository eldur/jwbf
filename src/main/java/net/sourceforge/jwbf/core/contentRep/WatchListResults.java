package net.sourceforge.jwbf.core.contentRep;

import java.util.List;

import net.sourceforge.jwbf.core.internal.Checked;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
@JsonInclude(Include.NON_DEFAULT)
public class WatchListResults {
    static class WatchContinue {
        private final String continueToken;

        @JsonCreator
        public WatchContinue(@JsonProperty("wlcontinue") String continueToken) {
            this.continueToken = continueToken;
        }

        public String getContinueToken() {
            return continueToken;
        }

        public boolean canContinue() {
            return continueToken != null;
        }
    }

    static class Query {
        private final List<WatchResponse> results;

        @JsonCreator
        public Query(@JsonProperty("watchlist") List<WatchResponse> results) {
            this.results = Checked.nonNull(results, "search results");
        }
    }

    private final WatchContinue watchContinue;
    private final String batchComplete;
    private final Query query;

    @JsonCreator
    public WatchListResults(@JsonProperty("continue") WatchContinue watchContinue,
        @JsonProperty("batchcomplete") String batchComplete, @JsonProperty("query") Query query) {
        this.watchContinue = watchContinue;
        this.batchComplete = batchComplete;
        this.query = Checked.nonNull(query, "watchlist results");
    }

    public boolean canContinue() {
        return watchContinue != null && watchContinue.canContinue();
    }

    public String getContinueToken(){
    	return watchContinue.getContinueToken();
    }

    public String getBatchComplete() {
        return batchComplete;
    }

    public List<WatchResponse> getResults() {
        return query.results;
    }

}
