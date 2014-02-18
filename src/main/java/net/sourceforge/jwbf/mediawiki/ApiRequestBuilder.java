package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.core.actions.RequestBuilder;

public class ApiRequestBuilder extends RequestBuilder {

  public ApiRequestBuilder() {
    super(MediaWiki.URL_API);
  }

  public ApiRequestBuilder action(String action) {
    param("action", action);
    return this;
  }

  /**
   * @deprecated use json instead (you have to change response handling)
   */
  @Deprecated
  public ApiRequestBuilder formatXml() {
    // TODO only json or xml
    param("format", "xml");
    return this;
  }

  public ApiRequestBuilder formatJson() {
    // TODO only json or xml
    param("format", "json");
    return this;
  }
}
