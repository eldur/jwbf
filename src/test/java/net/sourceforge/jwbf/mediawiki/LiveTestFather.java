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
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import net.sourceforge.jwbf.TestHelper;

import org.junit.ComparisonFailure;

import com.google.common.collect.Lists;

/**
 * @author Thomas Stock
 */
public class LiveTestFather extends TestHelper {

  private static final Properties data = new Properties();

  private static String filename = "";

  private static final Collection<String> specialChars = Lists.newArrayList();

  private LiveTestFather() {
  }

  static {
    specialChars.add("\"");
    specialChars.add("\'");
    specialChars.add("?");
    specialChars.add("%");
    specialChars.add("&");
    specialChars.add("[");
    specialChars.add("]");

    // find jwftestfile
    Collection<String> filepos = new Vector<String>();
    filepos.add(System.getProperty("user.home") + "/.jwbf/test.xml");
    filepos.add(System.getProperty("user.home") + "/jwbftest.xml");
    filepos.add("test.xml");
    for (String fname : filepos) {
      if (new File(fname).canRead()) {
        filename = fname;
        System.out.println("use testfile: " + filename);

        break;
      }
    }
    if (filename.length() < 1) {
      System.err.println("no testfile found. Use: "
          + System.getProperty("user.home") + "/.jwbf/test.xml");
      filename = System.getProperty("user.home") + "/.jwbf/test.xml";
    }

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e2) {
      e2.printStackTrace();
    }
    try {
      data.loadFromXML(new FileInputStream(filename));
    } catch (InvalidPropertiesFormatException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      File f = new File(filename);
      try {
        f.createNewFile();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * 
   * @return the current UTC
   */
  public static Date getCurrentUTC() {
    long currentDate = System.currentTimeMillis();
    TimeZone tz = TimeZone.getDefault();
    Calendar localCal = Calendar.getInstance(tz);
    localCal.setTimeInMillis(currentDate - tz.getOffset(currentDate));

    return new Date(localCal.getTimeInMillis());

  }

  private static void addEmptyKey(String key) {
    data.put(key, " ");
    try {
      data.storeToXML(new FileOutputStream(filename), "");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getValue(final String key) {
    if (!data.containsKey(key) || data.getProperty(key).trim().length() <= 0) {
      addEmptyKey(key);

      throw new ComparisonFailure("No or empty value for key: \"" + key
          + "\" in " + filename, key, filename);
    }
    return data.getProperty(key);
  }

  public static Collection<String> getSpecialChars() {
    return specialChars;
  }

}
