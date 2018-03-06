package net.sourceforge.jwbf.core.actions;

import java.nio.charset.Charset;
import java.util.Objects;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimaps;

import net.sourceforge.jwbf.core.actions.util.HttpAction;
import net.sourceforge.jwbf.core.internal.NonnullFunction;

public class Post extends HttpBase implements HttpAction {

  private static Function<Supplier<Object>, Object> flattenSuppliers =
      new NonnullFunction<Supplier<Object>, Object>() {
        @Nonnull
        @Override
        public Object applyNonnull(@Nonnull Supplier<Object> input) {
          return input.get();
        }
      };

  private final Supplier<String> req;
  private final ImmutableMultimap.Builder<String, Supplier<Object>> params;
  private final Charset charset;

  /** @deprecated use net.sourceforge.jwbf.core.actions.RequestBuilder */
  @Deprecated
  Post(String req, String charset) {
    this(Suppliers.ofInstance(req), charset);
  }

  /** @deprecated use net.sourceforge.jwbf.core.actions.RequestBuilder */
  @Deprecated
  Post(Supplier<String> req, String charset) {
    this(req, Charset.forName(charset), Optional.<ParamJoiner>absent());
  }

  private Post(
      Supplier<String> req,
      Charset charset,
      Optional<ParamJoiner> joiner,
      ImmutableMultimap.Builder<String, Supplier<Object>> params) {
    super(joiner);
    this.req = req;
    this.charset = charset;
    this.params = params;
  }

  Post(Supplier<String> req, Charset charset, Optional<ParamJoiner> joiner) {
    super(joiner);
    this.req = req;
    this.charset = charset;
    if (joiner.isPresent()) {
      this.params = joiner.get().postParams();
    } else {
      this.params = ImmutableMultimap.builder();
    }
  }

  private Post(Supplier<String> url, Charset charset) {
    this(url, charset, Optional.<ParamJoiner>absent());
  }

  /** Use utf-8 as default charset */
  Post(String url) {
    this(Suppliers.ofInstance(url));
  }

  private Post(Supplier<String> url) {
    this(url, Charsets.UTF_8);
  }

  /** @deprecated use {@link #postParam(String, Object)} */
  @Deprecated
  public void addParam(String key, Object value) {
    postParam(key, value);
  }

  /**
   * @deprecated use request builder instead, else it is difficult to apply request mutation for
   *     server based throttling
   */
  @Deprecated
  @CheckReturnValue(when = When.NEVER)
  public Post postParam(String key, Object value) {
    params.put(key, Suppliers.ofInstance(value));
    return new Post(req, charset, Optional.<ParamJoiner>absent(), params);
  }

  public ImmutableMultimap<String, Object> getParams() {
    return ImmutableMultimap.copyOf(Multimaps.transformValues(params.build(), flattenSuppliers));
  }

  @Override
  public String getRequest() {
    return req.get();
  }

  @Override
  public String getCharset() {
    return charset.displayName();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRequest(), getCharset(), getParams());
  }

  @Override
  public String toString() {
    return getRequest() + " " + getCharset() + " " + getParams();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (this == obj) {
      return true;
    } else if (obj instanceof Post) {
      Post that = (Post) obj;
      return Objects.equals(this.getRequest(), that.getRequest())
          && //
          Objects.equals(this.getCharset(), that.getCharset())
          && //
          Objects.equals(this.getParams(), that.getParams());
    } else {
      return false;
    }
  }
}
