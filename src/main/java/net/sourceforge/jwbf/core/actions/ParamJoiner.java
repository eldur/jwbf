package net.sourceforge.jwbf.core.actions;

import javax.annotation.Nonnull;

import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import net.sourceforge.jwbf.core.internal.NonnullFunction;

class ParamJoiner implements Supplier<String> {

  private static final Function<Entry<String, Supplier<String>>, String> TO_KEY_VALUE_PAIR = //
      new NonnullFunction<Entry<String, Supplier<String>>, String>() {

        @Nonnull
        @Override
        public String applyNonnull(@Nonnull Entry<String, Supplier<String>> input) {
          return input.getKey() + "=" + input.getValue().get();
        }
      };

  private final String path;
  private final ImmutableMap.Builder<String, Supplier<String>> params;
  private final ImmutableMap.Builder<String, Supplier<Object>> postParams;

  ParamJoiner(String path, //
      ImmutableMap.Builder<String, Supplier<String>> params, //
      ImmutableMap.Builder<String, Supplier<Object>> postParams) {
    this.path = path;
    this.params = params;
    this.postParams = postParams;
  }

  RequestBuilder toBuilder() {
    return RequestBuilder.of(path).params(params).postParams(postParams);
  }

  @Override
  public String get() {
    ImmutableMap<String, Supplier<String>> build = params.build();
    if (!build.isEmpty()) {
      ImmutableList<String> values = FluentIterable.from(build.entrySet()) //
          .transform(TO_KEY_VALUE_PAIR) //
          .toSortedList(Ordering.natural()) //
          ;
      return path + "?" + Joiner.on("&").join(values);
    }
    return path;
  }

  ImmutableMap.Builder<String, Supplier<Object>> postParams() {
    return postParams;
  }
}
