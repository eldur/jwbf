package net.sourceforge.jwbf.core.contentRep;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import net.sourceforge.jwbf.core.internal.Checked;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
@JsonInclude(Include.NON_DEFAULT)
public class SearchResultList {
  static class SearchContinue {
    private final int offset;
    private final String continueToken;

    @JsonCreator
    public SearchContinue(
        @JsonProperty("sroffset") int offset, @JsonProperty("continue") String continueToken) {
      this.offset = offset;
      this.continueToken = continueToken;
    }

    public int getOffset() {
      return offset;
    }

    public String getContinueToken() {
      return continueToken;
    }

    public boolean canContinue() {
      return continueToken != null;
    }
  }

  static class SearchInfo {
    private final int totalHits;
    private final String suggestion;

    @JsonCreator
    public SearchInfo(
        @JsonProperty("totalhits") int totalHits, @JsonProperty("suggestion") String suggestion) {
      this.totalHits = totalHits;
      this.suggestion = suggestion;
    }

    public long getTotalHits() {
      return totalHits;
    }

    public String getSuggestion() {
      return suggestion;
    }
  }

  static class Query {
    private final SearchInfo searchInfo;
    private final List<SearchResult> results;

    @JsonCreator
    public Query(
        @JsonProperty("searchinfo") SearchInfo searchInfo,
        @JsonProperty("search") List<SearchResult> results) {
      this.searchInfo = Checked.nonNull(searchInfo, "search info");
      this.results = Checked.nonNull(results, "search results");
    }
  }

  private final SearchContinue searchContinue;
  private final String batchComplete;
  private final Query query;

  @JsonCreator
  public SearchResultList(
      @JsonProperty("continue") SearchContinue searchContinue,
      @JsonProperty("batchcomplete") String batchComplete,
      @JsonProperty("query") Query query) {
    this.searchContinue = searchContinue;
    this.batchComplete = batchComplete;
    this.query = Checked.nonNull(query, "search query");
  }

  public boolean canContinue() {
    return searchContinue != null && searchContinue.canContinue();
  }

  public int getOffset() {
    return searchContinue.offset;
  }

  public String getBatchComplete() {
    return batchComplete;
  }

  public int getTotalHits() {
    return query.searchInfo.totalHits;
  }

  public String getSuggestion() {
    return query.searchInfo.suggestion;
  }

  public List<SearchResult> getResults() {
    return query.results;
  }
}
