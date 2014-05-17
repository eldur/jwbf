package net.sourceforge.jwbf.core.actions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import net.sourceforge.jwbf.core.actions.util.HttpAction;

public class Post implements HttpAction {

  private final String req;
  private final Builder<String, Object> params = ImmutableMap.<String, Object>builder();
  private final String charset;

  public Post(String req, String charset) {
    this.req = req;
    this.charset = charset;
  }

  /**
   * Use utf-8 as default charset
   */
  public Post(String url) {
    this(url, "utf-8");
  }

  /**
   * @deprecated use {@link #postParam(String, Object)}
   */
  @Deprecated
  public void addParam(String key, Object value) {
    postParam(key, value);
  }

  public Post postParam(String key, Object value) {
    params.put(key, value);
    return this;
  }

  public ImmutableMap<String, Object> getParams() {
    return params.build();
  }

  @Override
  public String getRequest() {
    return req;
  }

  @Override
  public String getCharset() {
    return charset;
  }

}
