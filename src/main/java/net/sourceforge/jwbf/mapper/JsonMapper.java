package net.sourceforge.jwbf.mapper;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
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

  public HashMap<String, Object> toMap(String json) {
    String nonNullJson = Checked.nonNull(json, "json");
    return Checked.nonNull(transfomer.toMap(nonNullJson), "a json mapping result");
  }

  public JsonNode toJsonNode(String json) {
    String nonNullJson = Checked.nonNull(json, "json");
    return Checked.nonNull(transfomer.toJsonNode(nonNullJson), "a json mapping result");
  }

  public interface ToJsonFunction {
    @Nonnull
    Object toJson(@Nonnull String jsonString, Class<?> clazz);

    JsonNode toJsonNode(String nonNullJson);

    HashMap<String, Object> toMap(@Nonnull String json);
  }

  static class JacksonToJsonFunction implements ToJsonFunction {

    ObjectMapper newObjectMapper() {
      ObjectMapper mapper = new ObjectMapper();
      // TODO: find a better way to do this
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      return mapper;
    }

    @Nonnull
    @Override
    public Object toJson(@Nonnull String jsonString, Class<?> clazz) {
      try {
        // ObjectMapper stores mutable data so each mapping uses a fresh instance
        //TODO: why are you creating a mapper for each call?
        return newObjectMapper().readValue(jsonString, clazz);
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
    }

    public HashMap<String, Object> toMap(@Nonnull String jsonString) {
      try {
        return newObjectMapper()
            .readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
            });
      } catch (IOException e) {
        throw new IllegalArgumentException();
      }
    }

    @Override
    public JsonNode toJsonNode(String json) {
      try {
        return newObjectMapper().readTree(json);
      } catch (IOException e) {
        throw new IllegalArgumentException();
      }
    }
  }
}
