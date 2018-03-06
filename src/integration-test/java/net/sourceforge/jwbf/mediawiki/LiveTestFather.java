/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */
package net.sourceforge.jwbf.mediawiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/** @author Thomas Stock */
public class LiveTestFather {

  private final SimpleMap data;

  private static final Logger log = LoggerFactory.getLogger(LiveTestFather.class);

  private static Collection<String> specialChars = Lists.newArrayList();

  static {
    specialChars.add("\"");
    specialChars.add("\'");
    specialChars.add("?");
    specialChars.add("%");
    specialChars.add("&");
    specialChars.add("[");
    specialChars.add("]");
  }

  private LiveTestFather() {
    data = new TestConfig();
  }

  public static boolean executeLiveTests() {
    boolean workWithDisk = optSysProperty("noLiveTests", false, true);
    if (workWithDisk) {
      throw new IllegalArgumentException("do not uses this toggle - use \"-DwithLiveTests\"");
    }
    return optSysProperty("withLiveTests", false, true);
  }

  private static boolean optSysProperty(String name, boolean defaultValue, boolean emptyValue) {
    String stringValue = System.getProperty(name, defaultValue + "");
    if (Strings.isNullOrEmpty(stringValue)) {
      return emptyValue;
    }
    return Boolean.valueOf(stringValue);
  }

  private static LiveTestFather instance;

  private static LiveTestFather get() {
    if (instance == null) {
      instance = new LiveTestFather();
    }
    return Preconditions.checkNotNull(instance, "do not run integ tests within normal testrun");
  }

  /** @return the current UTC */
  public static Date getCurrentUTC() {
    long currentDate = System.currentTimeMillis();
    TimeZone tz = TimeZone.getDefault();
    Calendar localCal = Calendar.getInstance(tz);
    localCal.setTimeInMillis(currentDate - tz.getOffset(currentDate));

    return new Date(localCal.getTimeInMillis());
  }

  private String getVal(String key) {
    String value = data.get(key);
    if (Strings.isNullOrEmpty(value)) {
      data.put(key, " ");
      log.warn("EMPTY value for " + key);
    }
    return data.get(key);
  }

  /** @deprecated avoid if possible */
  @Deprecated
  public static String getValueOrSkip(final String key) {
    skipIfIsNoIntegTest();
    return getValue(key);
  }

  public static String getValue(final String key) {
    return get().getVal(key);
  }

  private interface SimpleMap {
    String get(String key);

    String put(String key, String value);
  }

  private static class TestConfig implements SimpleMap {

    private final Properties properties;
    private final String filename;

    public TestConfig() {
      properties = new Properties();
      filename = findTestConfig();
      readOrCreate(filename);
    }

    private void write(String filename) {
      synchronized (this) {
        try {
          properties.storeToXML(new FileOutputStream(filename), "");
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }

    private void readOrCreate(String filename) {
      synchronized (this) {
        try {
          properties.loadFromXML(new FileInputStream(filename));
        } catch (InvalidPropertiesFormatException e) {
          throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
          File f = new File(filename);
          try {
            Files.createParentDirs(f);
            if (!f.createNewFile()) {
              throw new IllegalStateException("could not create file " + f.getAbsolutePath());
            }
          } catch (IOException e1) {
            throw new IllegalStateException(e);
          }
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
    }

    private String findTestConfig() {
      String home = System.getProperty("user.home");

      List<String> testConfigFilenames = Lists.newArrayList();
      testConfigFilenames.add(home + "/.jwbf/test.xml");
      testConfigFilenames.add(home + "/jwbftest.xml");
      testConfigFilenames.add("test.xml");

      for (String fname : testConfigFilenames) {
        if (new File(fname).canRead()) {
          log.info("use testfile: " + fname);
          return fname;
        }
      }
      String preferredConfigPosition = Iterables.getFirst(testConfigFilenames, null);
      log.warn("no testfile found. Use: " + preferredConfigPosition);
      return preferredConfigPosition;
    }

    @Override
    public String get(String key) {
      String value = properties.getProperty(key);
      if (!Strings.isNullOrEmpty(value)) {
        return value.trim();
      }
      return value;
    }

    @Override
    public String put(String key, String value) {
      String result = (String) properties.put(key, value);
      write(filename);
      return result;
    }
  }

  public static Collection<String> getSpecialChars() {
    return specialChars;
  }

  public static void skipIfIsNoIntegTest() {
    Assume.assumeTrue(LiveTestFather.executeLiveTests());
  }
}
