package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.core.RequestBuilder;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;

public class ApiRequestBuilder extends RequestBuilder {

  public ApiRequestBuilder() {
    super(MediaWiki.URL_API);
  }

  public ApiRequestBuilder action(String action) {
    param("action", action);
    return this;
  }

  public ApiRequestBuilder formatXml() {
    param("format", "xml");
    return this;
  }
}
