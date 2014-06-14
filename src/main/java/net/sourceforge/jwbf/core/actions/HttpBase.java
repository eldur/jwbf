package net.sourceforge.jwbf.core.actions;

import com.google.common.base.Optional;

abstract class HttpBase {

  private final Optional<ParamJoiner> joiner;

  public HttpBase(Optional<ParamJoiner> joiner) {
    this.joiner = joiner;
  }

  public RequestBuilder toBuilder() {
    if (joiner.isPresent()) {
      return joiner.get().toBuilder();
    } else {
      throw new UnsupportedOperationException(
          "only supported when type was created with a builder");
    }
  }
}
