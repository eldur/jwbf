package net.sourceforge.jwbf.core.actions;

import java.nio.charset.Charset;
import java.util.Objects;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.sourceforge.jwbf.core.actions.util.HttpAction;

public class Get extends HttpBase implements HttpAction {

  private final Supplier<String> req;
  private final Charset charset;

  /**
   * @param req like index.html?parm=value
   * @param charset like utf-8
   */
  Get(String req, String charset) {
    this(Suppliers.ofInstance(req), charset);
  }

  Get(Supplier<String> req, String charset) {
    this(req, Charset.forName(charset), Optional.<ParamJoiner>absent());
  }

  Get(Supplier<String> req, Charset charset, Optional<ParamJoiner> paramJoiner) {
    super(paramJoiner);
    this.req = req;
    this.charset = charset;
  }

  /** Use utf-8 as default charset. */
  Get(String url) {
    this(Suppliers.ofInstance(url), Charsets.UTF_8, Optional.<ParamJoiner>absent());
  }

  Get(ParamJoiner joiner) {
    this(joiner, Charsets.UTF_8, Optional.of(joiner));
  }

  /** {@inheritDoc} */
  @Override
  public String getRequest() {
    return getRequestAndFixAllSuppliers();
  }

  /** {@inheritDoc} */
  @Override
  public String getCharset() {
    return charset.displayName();
  }

  @Override
  public String toString() {
    return getRequestAndFixAllSuppliers() + " " + getCharset();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRequest(), getCharset());
  }

  /** if you look at the content, you can't change it anymore. */
  private String getRequestAndFixAllSuppliers() {
    return req.get();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (this == obj) {
      return true;
    } else if (obj instanceof Get) {
      Get that = (Get) obj;
      return Objects.equals(
              this.getRequestAndFixAllSuppliers(), //
              that.getRequestAndFixAllSuppliers())
          && //
          Objects.equals(this.getCharset(), that.getCharset());
    } else {
      return false;
    }
  }
}
