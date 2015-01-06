package net.sourceforge.jwbf.mediawiki.actions.queries;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import net.sourceforge.jwbf.core.internal.Checked;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
public class WatchListResults {
  static class WatchContinue {
    /** The continue token */
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

  static class Error {
    private final String code;
    private final String message;
    private final String moreInfo;

    @JsonCreator
    public Error(@JsonProperty("code") String code, @JsonProperty("info") String message,
        @JsonProperty("*") String moreInfo) {
      this.code = code;
      this.message = message;
      this.moreInfo = moreInfo;
    }

    public String getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

    public String getMoreInfo() {
      return moreInfo;
    }
  }

  private final WatchContinue watchContinue;
  private final String batchComplete;
  private final Error error;
  private final Query query;

  @JsonCreator
  public WatchListResults(@JsonProperty("continue") WatchContinue watchContinue,
      @JsonProperty("batchcomplete") String batchComplete, @JsonProperty("error") Error error,
      @JsonProperty("query") Query query) {
    this.watchContinue = watchContinue;
    this.batchComplete = batchComplete;
    this.error = error;
    this.query = Checked.nonNull(query, "watchlist results");
  }

  public boolean canContinue() {
    return watchContinue != null && watchContinue.canContinue();
  }

  public String getContinueToken() {
    return watchContinue.getContinueToken();
  }

  public String getBatchComplete() {
    return batchComplete;
  }

  public Error getError() {
    return error;
  }

  public List<WatchResponse> getResults() {
    return query.results;
  }

}
