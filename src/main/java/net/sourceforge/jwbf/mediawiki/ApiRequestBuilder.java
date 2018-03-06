package net.sourceforge.jwbf.mediawiki;

import javax.annotation.CheckForNull;

import net.sourceforge.jwbf.core.actions.ParamTuple;
import net.sourceforge.jwbf.core.actions.RequestBuilder;

public class ApiRequestBuilder extends RequestBuilder {

  public static final ParamTuple<String> NEW_CONTINUE = //
      new ParamTuple<>("continue", MediaWiki.urlEncode("-||"));

  public ApiRequestBuilder() {
    super(MediaWiki.URL_API);
  }

  public ApiRequestBuilder action(String action) {
    param("action", action);
    return this;
  }

  /** @deprecated prefer json format */
  @Deprecated
  public ApiRequestBuilder formatXml() {
    param("format", "xml");
    return this;
  }

  public ApiRequestBuilder formatJson() {
    param("format", "json");
    return this;
  }

  /**
   * https://www.mediawiki.org/wiki/API:Query#Continuing_queries
   *
   * @param version Continuing queries this way will only work with MW1_21 or higher
   */
  public ApiRequestBuilder paramNewContinue(@CheckForNull MediaWiki.Version version) {
    if (version != null && version.greaterEqThen(MediaWiki.Version.MW1_21)) {
      param(NEW_CONTINUE);
    }
    return this;
  }
}
