package net.sourceforge.jwbf.core.actions;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;

public class RequestBuilder {

  private final ImmutableMultimap.Builder<String, Supplier<String>> params =
      ImmutableMultimap.builder();
  private final ImmutableMultimap.Builder<String, Supplier<Object>> postParams =
      ImmutableMultimap.builder();
  private final String path;

  public RequestBuilder(String path) {
    this.path = path;
  }

  public RequestBuilder param(String key, int value) {
    param(key, value + "");
    return this;
  }

  public RequestBuilder param(String key, Supplier<String> stringSupplier) {
    return applyKeyValueTo(key, stringSupplier, params);
  }

  public RequestBuilder param(ParamTuple<String> paramTuple) {
    return param(paramTuple.key, paramTuple.valueSupplier);
  }

  private <T> RequestBuilder applyKeyValueTo(String key, Supplier<?> stringSupplier,
      ImmutableMultimap.Builder<String, Supplier<T>> toParams) {
    if (!Strings.isNullOrEmpty(key)) {
      Preconditions.checkNotNull(stringSupplier);

      Supplier<?> memoize = new HashCodeEqualsMemoizingSupplier<>(stringSupplier);
      if (!toParams.build().containsEntry(key, memoize)) {
        toParams.put(key, (Supplier<T>) memoize);
      }
    }
    return this;
  }

  RequestBuilder postParams(ImmutableMultimap.Builder<String, Supplier<Object>> all) {
    postParams.putAll(all.build());
    return this;
  }

  RequestBuilder params(ImmutableMultimap.Builder<String, Supplier<String>> params) {
    this.params.putAll(params.build());
    return this;
  }

  private RequestBuilder postParam(String key, Object value) {
    return applyKeyValueTo(key, Suppliers.ofInstance(value), postParams);
  }

  public RequestBuilder postParam(String key, String value) {
    return postParam(key, (Object) value);
  }

  public RequestBuilder postParam(String key, int value) {
    return postParam(key, value + "");
  }

  public RequestBuilder postParam(String key, double value, Locale locale, String format) {
    return postParam(key, String.format(locale, format, value));
  }

  public RequestBuilder postParam(String key, File value) {
    return postParam(key, (Object) value);
  }

  public RequestBuilder postParam(ParamTuple<?> paramTuple) {
    Supplier<? extends Object> val = paramTuple.valueSupplier;
    return applyKeyValueTo(paramTuple.key, val, postParams);
  }

  public RequestBuilder param(String key, boolean value) {
    return param(key, Boolean.toString(value));
  }

  public RequestBuilder param(String key, String value) {
    if (!Strings.isNullOrEmpty(key)) {
      if (Strings.isNullOrEmpty(value)) {
        param(key, Suppliers.ofInstance("None"));
      } else {
        param(key, Suppliers.ofInstance(value));
      }

    }
    return this;
  }

  public Post buildPost() {
    return new Post(lazy(), Charsets.UTF_8, Optional.of(lazy()));
  }

  public Get buildGet() {
    return new Get(lazy());
  }

  ParamJoiner lazy() {
    return new ParamJoiner(path, params, postParams);
  }

  public String build() {
    return lazy().get();
  }

  public static RequestBuilder of(String path) {
    return new RequestBuilder(path);
  }

  // see Guava Suppliers.memoize()
  static class HashCodeEqualsMemoizingSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    transient volatile boolean initialized;
    // "value" does not need to be volatile; visibility piggy-backs
    // on volatile read of "initialized".
    transient T value;

    HashCodeEqualsMemoizingSupplier(Supplier<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public T get() {
      // A 2-field variant of Double Checked Locking.
      if (!initialized) {
        synchronized (this) {
          if (!initialized) {
            T t = delegate.get();
            value = t;
            initialized = true;
            return t;
          }
        }
      }
      return value;
    }

    private static final long serialVersionUID = 0;

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof HashCodeEqualsMemoizingSupplier) {
        return delegate.equals(((HashCodeEqualsMemoizingSupplier) obj).delegate);
      } else {
        throw new IllegalStateException(obj.getClass().getCanonicalName());
      }
    }
  }
}
