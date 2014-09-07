package net.sourceforge.jwbf.mapper;

import javax.annotation.Nonnull;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.jwbf.core.internal.Checked;

public class JsonMapper {

  private final ToJsonFunction transfomer;

  public JsonMapper() {
    this(new JacksonToJsonFunction());
  }

  public <T> JsonMapper(ToJsonFunction transfomer) {
    this.transfomer = transfomer;
  }

  public <T> T get(String json, Class<T> clazz) {
    String nonNullJson = Checked.nonNull(json, "json");
    return (T) Checked.nonNull(transfomer.toJson(nonNullJson, clazz), "a json mapping result");
  }

  public interface ToJsonFunction {
    @Nonnull
    Object toJson(@Nonnull String jsonString, Class<?> clazz);
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
