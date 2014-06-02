package net.sourceforge.jwbf.core.actions;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Ordering;

class ParamJoiner implements Supplier<String> {

  private static final Function<Map.Entry<String, Supplier<String>>, String> TO_KEY_VALUE_PAIR = //
      new Function<Map.Entry<String, Supplier<String>>, String>() {

        @Override
        public String apply(Map.Entry<String, Supplier<String>> input) {
          Map.Entry<String, Supplier<String>> nonNullIn = Preconditions.checkNotNull(input);
          return nonNullIn.getKey() + "=" + nonNullIn.getValue().get();
        }
      };

  private final String path;
  private final ImmutableMultimap.Builder<String, Supplier<String>> params;
  private final ImmutableMultimap.Builder<String, Supplier<Object>> postParams;

  ParamJoiner(String path, //
      ImmutableMultimap.Builder<String, Supplier<String>> params, //
      ImmutableMultimap.Builder<String, Supplier<Object>> postParams) {
    this.path = path;
    this.params = params;
    this.postParams = postParams;
  }

  RequestBuilder toBuilder() {
    return RequestBuilder.of(path).params(params).postParams(postParams);
  }

  @Override
  public String get() {
    ImmutableMultimap<String, Supplier<String>> build = params.build();
    if (!build.isEmpty()) {
      ImmutableList<String> values = FluentIterable.from(build.entries()) //
          .transform(TO_KEY_VALUE_PAIR) //
          .toSortedList(Ordering.natural()) //
          ;
      return path + "?" + Joiner.on("&").join(values);
    }
    return path;
  }

  ImmutableMultimap.Builder<String, Supplier<Object>> postParams() {
    return postParams;
  }
}
