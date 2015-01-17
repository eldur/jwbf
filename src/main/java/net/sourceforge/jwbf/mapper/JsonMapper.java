package net.sourceforge.jwbf.mapper;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Nonnull;

import net.sourceforge.jwbf.core.internal.Checked;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public interface ToJsonFunction {
        @Nonnull
        Object toJson(@Nonnull String jsonString, Class<?> clazz);

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
                return newObjectMapper().readValue(jsonString, clazz);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public HashMap<String, Object> toMap(@Nonnull String jsonString) {
            try {
                return newObjectMapper().readValue(jsonString,
                        new TypeReference<HashMap<String, Object>>() {
                        });
            } catch (IOException e) {
                throw new IllegalArgumentException();
            }
        }
    }
}
