package net.sourceforge.jwbf.core;

import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.jwbf.core.actions.Get;
import net.sourceforge.jwbf.core.actions.Post;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

public class RequestBuilder {

  private static final Function<Entry<String, String>, String> TO_KEY_VALUE_PAIR = new Function<Map.Entry<String, String>, String>() {

    @Override
    public String apply(Entry<String, String> input) {
      Entry<String, String> nonNullIn = Preconditions.checkNotNull(input);
      return nonNullIn.getKey() + "=" + nonNullIn.getValue();
    }
  };
  private final Multimap<String, String> params = ArrayListMultimap.create();
  private final String path;

  public RequestBuilder(String path) {
    this.path = path;
  }

  public RequestBuilder param(String key, int value) {
    param(key, value + "");
    return this;
  }

  public RequestBuilder param(String key, String value) {
    if (!Strings.isNullOrEmpty(key)) {
      if (Strings.isNullOrEmpty(value)) {
        value = "None";
      }
      if (!params.containsEntry(key, value)) {
        params.put(key, value);
      }
    }
    return this;
  }

  public Post buildPost() {
    return new Post(build());
  }

  public Get buildGet() {
    return new Get(build());
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

}
