package net.sourceforge.jwbf.core.actions;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

public class RequestBuilder {

  private static final Function<Entry<String, Supplier<String>>, String> TO_KEY_VALUE_PAIR = //
      new Function<Map.Entry<String, Supplier<String>>, String>() {

        @Override
        public String apply(Entry<String, Supplier<String>> input) {
          Entry<String, Supplier<String>> nonNullIn = Preconditions.checkNotNull(input);
          return nonNullIn.getKey() + "=" + nonNullIn.getValue().get();
        }
      };
  private final Multimap<String, Supplier<String>> params = ArrayListMultimap.create();
  private final String path;

  public RequestBuilder(String path) {
    this.path = path;
  }

  public RequestBuilder param(String key, int value) {
    param(key, value + "");
    return this;
  }

  public RequestBuilder param(String key, Supplier<String> stringSupplier) {
    if (!Strings.isNullOrEmpty(key)) {
      Preconditions.checkNotNull(stringSupplier);

      Supplier<String> memoize = new HashCodeEqualsMemoizingSupplier<>(stringSupplier);
      if (!params.containsEntry(key, memoize)) {
        params.put(key, memoize);
      }
    }
    return this;
  }

  public RequestBuilder param(String key, String value) {
    if (!Strings.isNullOrEmpty(key)) {
      if (Strings.isNullOrEmpty(value)) {
        value = "None";
      }
      param(key, Suppliers.ofInstance(value));
    }
    return this;
  }

  public Post buildPost() {
    return new Post(lazy());
  }

  public Get buildGet() {
    return new Get(lazy());
  }

  Supplier<String> lazy() {
    return new Supplier<String>() {
      @Override
      public String get() {
        return build();
      }
    };
  }

  public String build() {

    String paramString = "";
    if (!params.isEmpty()) {
      ImmutableList<String> values = FluentIterable.from(params.entries()) //
          .transform(TO_KEY_VALUE_PAIR) //
          .toSortedList(Ordering.natural()) //
          ;

      paramString = "?" + Joiner.on("&").join(values);
    }
    return path + paramString;
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
        throw new IllegalStateException();
      }
    }
  }
}
