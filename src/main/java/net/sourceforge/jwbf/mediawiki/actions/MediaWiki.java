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
 * Carlos Valenzuela
 */
package net.sourceforge.jwbf.mediawiki.actions;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * 
 * @author Thomas Stock
 * 
 */
public final class MediaWiki {

  static final String CHARSET = "UTF-8";

  public static final String URL_API = "/api.php";
  public static final String URL_INDEX = "/index.php";

  public static final int NS_MAIN = 0;
  public static final int NS_MAIN_TALK = 1;
  public static final int NS_USER = 2;
  public static final int NS_USER_TALK = 3;
  public static final int NS_META = 4;
  public static final int NS_META_TALK = 5;
  public static final int NS_IMAGES = 6;
  public static final int NS_IMAGES_TALK = 7;
  public static final int NS_MEDIAWIKI = 8;
  public static final int NS_MEDIAWIKI_TALK = 9;
  public static final int NS_TEMPLATE = 10;
  public static final int NS_TEMPLATE_TALK = 11;
  public static final int NS_HELP = 12;
  public static final int NS_HELP_TALK = 13;
  public static final int NS_CATEGORY = 14;
  public static final int NS_CATEGORY_TALK = 15;

  public static final int[] NS_ALL = { //
  NS_MAIN, NS_MAIN_TALK, NS_USER, NS_USER_TALK, NS_META, NS_META_TALK, NS_IMAGES, NS_IMAGES_TALK,
      NS_MEDIAWIKI, NS_MEDIAWIKI_TALK, NS_TEMPLATE, NS_TEMPLATE_TALK, NS_HELP, NS_HELP_TALK,
      NS_CATEGORY, NS_CATEGORY_TALK //
  };

  public static final Set<String> BOT_GROUPS;

  static {

    BOT_GROUPS = ImmutableSet.of("bot");
  }

  /**
   * Representaion of MediaWiki version.
   * 
   * @author Thomas Stock
   * 
   */
  public enum Version {
    UNKNOWN
    /**
     * Released 2009-06
     */
    , MW1_15
    /**
     * Released 2010-07
     */
    , MW1_16
    /**
     * Released 2011-06
     */
    , MW1_17
    /**
     * Released 2011-11
     */
    , MW1_18
    /**
     * Released 2012-05
     */
    , MW1_19
    /**
     * Released 2012-11
     */
    , MW1_20
    /**
     * 
     */
    , DEVELOPMENT;

    private static Version last = UNKNOWN;

    /**
     * 
     * @return a, like 1.15
     */
    public String getNumber() {
      return name().replace("MW", "").replace("_0", "_").replace("_", ".");
    }

    private int getIntValue() {
      try {
        return Integer.parseInt(getNumber().replace(".", ""));
      } catch (Exception e) {
        if (this == DEVELOPMENT)
          return Integer.MAX_VALUE;
        return -1;
      }
    }

    /**
     * 
     * @return the latest version
     */
    public static Version getLatest() {
      if (last == UNKNOWN) {
        Version[] as = valuesStable();
        for (int i = 0; i < as.length; i++) {
          if (as[i].getIntValue() > last.getIntValue()) {
            last = as[i];
          }
        }
      }
      return last;
    }

    /**
     * @return true if
     */
    public boolean greaterEqThen(Version v) {
      return (v.getIntValue() <= getIntValue());
    }

    /**
     * @return all known stable MW Versions
     */
    public static Version[] valuesStable() {
      List<Version> resultVersions = Lists.newArrayList();

      for (Version version : Version.values()) {
        boolean isDeprecated = getField(version).isAnnotationPresent(Deprecated.class);
        if (!(version.equals(DEVELOPMENT) || version.equals(UNKNOWN) || isDeprecated)) {
          resultVersions.add(version);
        }
      }
      return resultVersions.toArray(new Version[0]);
    }

    protected static Field getField(Version version) {
      try {
        return version.getClass().getField(version.name());
      } catch (NoSuchFieldException nsfe) {
        throw new IllegalStateException(nsfe);
      }
    }

  }

  private MediaWiki() {
    // do nothing
  }

  public static String getCharset() {
    return CHARSET;
  }

  /**
   * @return encoded s
   */
  public static String encode(String s) {
    try {
      return URLEncoder.encode(s, MediaWiki.CHARSET);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return decoded s
   */
  public static String decode(final String s) {
    String out = HTMLEntities.unhtmlentities(s);
    out = HTMLEntities.unhtmlQuotes(out);
    return out;
  }
}
