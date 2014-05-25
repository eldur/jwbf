package net.sourceforge.jwbf.mapper;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {

  public <T> T get(String content, Class<T> clazz) {
    T result;
    try {
      result = (T) newObjectMapper().readValue(content, clazz);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    return result;
  }

  ObjectMapper newObjectMapper() {
    return new ObjectMapper();
  }

}
