package net.sourceforge.jwbf.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import javax.annotation.Nonnull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import net.sourceforge.jwbf.JWBF;

import org.junit.Before;
import org.junit.Test;

public class JsonMapperTest {

  JsonMapper testee;

  @Before
  public void before() {
    testee = new JsonMapper();
  }

  @Test
  public void testGet() {
    // GIVEN
    String content =
        getContent(JWBF.urlToFile(Resources.getResource("mediawiki/v1-22/siteinfo.json")));

    // WHEN
    SiteInfoData siteInfoData = testee.get(content, SiteInfoData.class);

    // THEN
    assertEquals("Main Page", siteInfoData.getMainpage());
  }

  @Test
  public void testNullInput() {
    try {
      // GIVEN / WHEN
      testee.get(null, SiteInfoData.class);
    } catch (NullPointerException npe) {
      // THEN
      assertEquals("json must not be null", npe.getMessage());
    }
  }

  @Test
  public void testNullResponse() {
    // GIVEN
    JsonMapper.ToJsonFunction nullFunction = new JsonMapper.ToJsonFunction() {

      @Nonnull
      @Override
      public Object toJson(@Nonnull String jsonString, Class<?> clazz) {
        return null;
      }

    @Override
    public HashMap<String, Object> toMap(String json) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonNode toJsonNode(String nonNullJson) {
        // TODO Auto-generated method stub
        return null;
    }
    };
    testee = new JsonMapper(nullFunction);
    try {
      // WHEN
      testee.get("any", SiteInfoData.class);
    } catch (NullPointerException npe) {
      // THEN
      assertEquals("a json mapping result must not be null", npe.getMessage());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGet_withException() {
    // GIVEN
    JsonMapper.JacksonToJsonFunction testee = new JsonMapper.JacksonToJsonFunction() {
      @Override
      ObjectMapper newObjectMapper() {
        ObjectMapper mock = mock(ObjectMapper.class);
        try {
          doThrow(IOException.class).when(mock).readValue(isA(String.class), isA(Class.class));
        } catch (IOException e) {
          fail();
        }
        return mock;
      }
    };

    // WHEN / THEN
    testee.toJson("", Object.class);
    fail();
  }

  String getContent(File file) {
    String content;
    try {
      content = Files.toString(file, Charsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return content;
  }

  private static class SiteInfoData {

    private final String mainpage;

    public SiteInfoData(String mainpage) {
      this.mainpage = mainpage;
    }

    @JsonCreator
    private static SiteInfoData newSiteInfoData(Map<String, Object> data) {
      Map<String, Object> query = (Map<String, Object>) data.get("query");
      Map<String, String> general = (Map<String, String>) query.get("general");
      String mainpage = general.get("mainpage");
      return new SiteInfoData(mainpage);
    }

    String getMainpage() {
      return mainpage;
    }

  }

}
