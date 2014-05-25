package net.sourceforge.jwbf.core.actions;

import java.util.Objects;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.sourceforge.jwbf.core.actions.util.HttpAction;

public class Get implements HttpAction {

  private final Supplier<String> req;
  private final String charset;

  /**
   * @param req     like index.html?parm=value
   * @param charset like utf-8
   */
  public Get(String req, String charset) {
    this(Suppliers.ofInstance(req), charset);
  }

  public Get(Supplier<String> req, String charset) {
    this.req = req;
    this.charset = charset;
  }

  /**
   * Use utf-8 as default charset.
   */
  public Get(String url) {
    this(Suppliers.ofInstance(url));
  }

  public Get(Supplier<String> url) {
    this(url, "utf-8");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getRequest() {
    return req.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCharset() {
    return charset;
  }

  @Override
  public String toString() {
    return getRequest() + " " + getCharset();
  }

  @Override
  public int hashCode() {
    return Objects.hash(charset, req);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (this == obj) {
      return true;
    } else if (obj instanceof Get) {
      Get that = (Get) obj;
      return Objects.equals(this.req, that.req) //
          && Objects.equals(this.charset, that.charset);
    } else {
      return false;
    }
  }

}
