package net.sourceforge.jwbf.mapper;

import javax.annotation.Nonnull;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

public class JsonMapper {

  private final ToJsonFunction transfomer;

  public JsonMapper() {
    this(new JacksonToJsonFunction());
  }

  public <T> JsonMapper(ToJsonFunction transfomer) {
    this.transfomer = transfomer;
  }

  public <T> T get(String json, Class<T> clazz) {
    String nonNullJson = Preconditions.checkNotNull(json, "do not convert null");
    return (T) Preconditions
        .checkNotNull(transfomer.toJson(nonNullJson, clazz), "a json mapping must not return null");
  }

  public static interface ToJsonFunction {
    @Nonnull
    public Object toJson(@Nonnull String jsonString, Class<?> clazz);
  }

  static class JacksonToJsonFunction implements ToJsonFunction {

    ObjectMapper newObjectMapper() {
      return new ObjectMapper();
    }

    @Nonnull
    @Override
    public Object toJson(@Nonnull String jsonString, Class<?> clazz) {
      try {
        return newObjectMapper().readValue(jsonString, clazz);
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}
