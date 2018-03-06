package net.sourceforge.jwbf.core.contentRep;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseStrategy.class)
@JsonInclude(Include.NON_DEFAULT)
public class SearchResult {
  @JsonProperty("ns")
  private final int ns;

  private final String title;
  private final int size;
  private final int wordCount;
  private final String timestamp;
  private final String snippet;
  private final String titleSnippet;
  private final String redirectSnippet;
  private final String redirectTitle;
  private final String sectionSnippet;
  private final String sectionTitle;

  @SuppressWarnings("checkstyle:parameternumber")
  public SearchResult(
      @JsonProperty("ns") int ns,
      @JsonProperty("title") String title,
      @JsonProperty("size") int size,
      @JsonProperty("wordcount") int wordCount,
      @JsonProperty("timestamp") String timestamp,
      @JsonProperty("snippet") String snippet,
      @JsonProperty("titlesnippet") String titleSnippet,
      @JsonProperty("redirectsnippet") String redirectSnippet,
      @JsonProperty("redirecttitle") String redirectTitle,
      @JsonProperty("sectionsnippet") String sectionSnippet,
      @JsonProperty("sectiontitle") String sectionTitle) {
    this.ns = ns;
    this.title = title;
    this.size = size;
    this.wordCount = wordCount;
    this.timestamp = timestamp;
    this.snippet = snippet;
    this.titleSnippet = titleSnippet;
    this.redirectSnippet = redirectSnippet;
    this.redirectTitle = redirectTitle;
    this.sectionSnippet = sectionSnippet;
    this.sectionTitle = sectionTitle;
  }

  public int getNamespace() {
    return ns;
  }

  public String getTitle() {
    return title;
  }

  /** size of the page in bytes. */
  public int getSize() {
    return size;
  }

  /** word count of the page. */
  public int getWordCount() {
    return wordCount;
  }

  /** timestamp of when the page was last edited. */
  // TODO use JodaTime instead?
  public String getTimestamp() {
    return timestamp;
  }

  /** parsed snippet of the page. */
  public String getSnippet() {
    return snippet;
  }

  /** Adds a parsed snippet of the page title. */
  public String getTitleSnippet() {
    return titleSnippet;
  }

  /** parsed snippet of the redirect title. */
  public String getRedirectSnippet() {
    return redirectSnippet;
  }

  /** title of the matching redirect. */
  public String getRedirectTitle() {
    return redirectTitle;
  }

  /** parsed snippet of the matching section title. */
  public String getSectionSnippet() {
    return sectionSnippet;
  }

  /** title of the matching section. */
  public String getSectionTitle() {
    return sectionTitle;
  }
}
