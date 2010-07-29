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
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Thomas Stock
 *
 */
public final class MediaWiki {

  /**
   * @deprecated use {@link #NS_MAIN} instead. Wrong value.
   */
  @Deprecated
  public static final int ARTICLE = 1 << 1;
  /**
   * @deprecated use {@link #NS_MAIN} instead. Wrong value.
   */
  @Deprecated
  public static final int MEDIA = 1 << 2;
  /**
   * @deprecated use {@link #NS_CATEGORY} instead.
   */
  @Deprecated
  public static final int SUBCATEGORY = 1 << 3;

  static final String CHARSET = "UTF-8";

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

  public static final int [] NS_ALL = {NS_MAIN, NS_MAIN_TALK, NS_USER, NS_USER_TALK
    , NS_META, NS_META_TALK, NS_IMAGES, NS_IMAGES_TALK, NS_MEDIAWIKI, NS_MEDIAWIKI_TALK
    , NS_TEMPLATE, NS_TEMPLATE_TALK, NS_HELP, NS_HELP_TALK, NS_CATEGORY, NS_CATEGORY_TALK};

  public static final Set<String> BOT_GROUPS = new HashSet<String>();

  static {
    BOT_GROUPS.add("bot");
  }

  /**
   * Representaion of MediaWiki version.
   * @author Thomas Stock
   *
   */
  public enum Version {
    MW1_09, MW1_10, MW1_11, MW1_12, MW1_13, MW1_14, MW1_15, MW1_16, UNKNOWN, DEVELOPMENT;


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
     * @return the last version
     */
    public static Version getLast() {
      if (last == UNKNOWN) {
        Version [] as = valuesStable();
        for (int i = 0; i < as.length; i++) {
          if (as[i].getIntValue() > last.getIntValue()) {
            last = as[i];
          }
        }
      }
      return last;
    }
    /**
     *
     * @param v a
     * @return true if
     */
    public boolean greaterEqThen(Version v) {
      if (v.getIntValue() > getIntValue())
        return false;
      return true;
    }
    /**
     *
     * @return all known stable MW Versions
     */
    public static Version [] valuesStable() {
      Version [] vxN = new Version [Version.values().length - 2];

      Version [] vx = Version.values();
      int j = 0;
      for (int i = 0; i < vx.length; i++) {
        if (!(vx[i].equals(DEVELOPMENT) || vx[i].equals(UNKNOWN))) {
          vxN[j++] = vx[i];
        }
      }
      return vxN;
    }

  }

  private MediaWiki() {
    //		do nothing
  }
  /**
   *
   * @return the
   */
  public static String getCharset() {
    return CHARSET;
  }
  /**
   *
   * @param s a
   * @return encoded s
   */
  public static String encode(String s) {
    try {
      return URLEncoder.encode(s, MediaWiki.CHARSET);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return s;
  }
  /**
   *
   * @param s a
   * @return decoded s
   */
  public static String decode(final String s) {
    String out = HTMLEntities.unhtmlentities(s);
    out = HTMLEntities.unhtmlQuotes(out);
    return out;
  }
}
