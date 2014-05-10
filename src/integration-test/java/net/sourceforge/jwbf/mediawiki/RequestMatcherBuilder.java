package net.sourceforge.jwbf.mediawiki;

import net.sourceforge.jwbf.AbstractIntegTest;

import com.github.dreamhead.moco.RequestMatcher;
import com.github.dreamhead.moco.matcher.AndRequestMatcher;
import com.google.common.collect.ImmutableList;

public class RequestMatcherBuilder {

  private final ImmutableList.Builder<RequestMatcher> matcherList = ImmutableList.builder();

  public RequestMatcherBuilder with(RequestMatcher element) {
    matcherList.add(element);
    return this;
  }

  public RequestMatcher build() {
    return AbstractIntegTest.onlyOnce(new AndRequestMatcher(matcherList.build()));
  }

}
