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
package net.sourceforge.jwbf.mediawiki;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import net.sourceforge.jwbf.core.NotReleased;
import net.sourceforge.jwbf.core.internal.Checked;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * @author Thomas Stock
 */
public final class MediaWiki {

  static final Charset CHARSET = StandardCharsets.UTF_8;

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

  public static final ImmutableList<Integer> NS_EVERY = //
      ImmutableList.<Integer>builder() //
          .add(NS_MAIN) //
          .add(NS_MAIN_TALK) //
          .add(NS_USER) //
          .add(NS_USER_TALK) //
          .add(NS_META) //
          .add(NS_META_TALK) //
          .add(NS_IMAGES) //
          .add(NS_IMAGES_TALK) //
          .add(NS_MEDIAWIKI) //
          .add(NS_MEDIAWIKI_TALK) //
          .add(NS_TEMPLATE) //
          .add(NS_TEMPLATE_TALK) //
          .add(NS_HELP) //
          .add(NS_HELP_TALK) //
          .add(NS_CATEGORY) //
          .add(NS_CATEGORY_TALK) //
          .build();

  /**
   * @deprecated prefer {@link #NS_EVERY}
   */
  @Deprecated
  public static final int[] NS_ALL = Ints.toArray(NS_EVERY);

  public static final Set<String> BOT_GROUPS;

  static {
    BOT_GROUPS = ImmutableSet.of("bot");
  }

  public static String pipeJoined(String... parts) {
    return pipeJoined(nullSafeCopyOf(parts));
  }

  public static String pipeJoined(ImmutableList<String> parts) {
    return pipeJoiner().join(parts);
  }

  public static Joiner pipeJoiner() {
    return Joiner.on("|");
  }

  /**
   * Representaion of MediaWiki version.
   *
   * @author Thomas Stock
   */
  public enum Version {
    /**
     * TODO add enum value
     */
    UNKNOWN
    /**
     *
     */
    , @Deprecated MW1_14
    /**
     * Released 2009-06
     */
    , @Deprecated MW1_15
    /**
     * Released 2010-07
     */
    , @Deprecated MW1_16
    /**
     * Released 2011-06
     */
    , @Deprecated MW1_17
    /**
     * Released 2011-11
     */
    , @Deprecated MW1_18
    /**
     * Released 2012-05
     */
    , MW1_19
    /**
     * Released 2012-11
     */
    , @Deprecated MW1_20
    /**
     * Released 2013-05-25
     */
    , @Deprecated MW1_21
    /**
     * Released 2013-12-06
     */
    , @Deprecated MW1_22
    /**
     * Released 2014-06-05
     */
    , MW1_23
    /**
     * Released 2014-11-26
     */
    , MW1_24
    /**
     * TODO Not released
     */
    , @NotReleased MW1_25
    /**
     *
     */
    , DEVELOPMENT;

    private static final ImmutableList<Version> STABLE_VERSIONS = FluentIterable //
        .from(Arrays.asList(Version.values())) //
        .filter(new Predicate<Version>() {
          @Override
          public boolean apply(@Nullable Version version) {
            return isStableVersion(version);
          }
        }) //
        .toSortedList(new Comparator<Version>() {
          @Override
          public int compare(Version o1, Version o2) {
            return Integer.valueOf(o1.getIntValue()).compareTo(Integer.valueOf(o2.getIntValue()));
          }
        });

    private static final Version LATEST_VERSION = Iterables.getLast(valuesStable());

    /**
     * @return a, like 1.15
     */
    public String getNumber() {
      return name().replace("MW", "").replace("_0", "_").replace("_", ".");
    }

    /**
     * @return like 1-15
     */
    public String getNumberVariation() {
      return getNumber().replace(".", "-");
    }

    private int getIntValue() {
      try {
        return Integer.parseInt(getNumber().replace(".", ""));
      } catch (NumberFormatException e) {
        if (this == DEVELOPMENT) {
          return Integer.MAX_VALUE;
        }
        return -1;
      }
    }

    /**
     * @return the latest version
     */
    public static Version getLatest() {
      return LATEST_VERSION;
    }

    @VisibleForTesting
    public static boolean isStableVersion(Version version) {
      if (version != null) {
        Field field = getField(version);
        boolean isDeprecated = field.isAnnotationPresent(Deprecated.class);
        boolean isBeta = field.isAnnotationPresent(NotReleased.class);
        return !(version.equals(DEVELOPMENT) || version.equals(UNKNOWN) || isDeprecated || isBeta);
      } else {
        return false;
      }
    }

    /**
     * @return true if
     */
    public boolean greaterEqThen(Version v) {
      return v.getIntValue() <= getIntValue();
    }

    /**
     * @return all known stable MW Versions
     */
    public static ImmutableList<Version> valuesStable() {
      return STABLE_VERSIONS;
    }

    static Field getField(Version version) {
      return getFieldUnchecked(Version.class, version.name());
    }
  }

  static Field getFieldUnchecked(Class<?> clazz, String fieldName) {
    try {
      return clazz.getField(fieldName);
    } catch (NoSuchFieldException nsfe) {
      throw new IllegalArgumentException(nsfe);
    }
  }

  private MediaWiki() {
    // do nothing
  }

  public static String getCharset() {
    return CHARSET.displayName();
  }

  public static String urlEncode(String s) {
    return urlEncodeUnchecked(s, MediaWiki.getCharset());
  }

  static String urlEncodeUnchecked(String in, String charset) {
    try {
      return URLEncoder.encode(Checked.nonNull(in, "in"), charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String urlDecode(String s) {
    return urlDecodeUnchecked(s, MediaWiki.getCharset());
  }

  static String urlDecodeUnchecked(String in, String charset) {
    try {
      return URLDecoder.decode(Checked.nonNull(in, "in"), charset);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static String htmlUnescape(final String s) {
    return StringEscapeUtils.unescapeHtml4(s);
  }

  /**
   * helper method generating a namespace string as required by the MW-api.
   *
   * @param namespaces namespace as
   * @return with numbers seperated by |
   * @deprecated prefer {@link #createNsString(java.util.List)}
   */
  @Deprecated
  public static String createNsString(int... namespaces) {
    return createNsString(nullSafeCopyOf(namespaces));
  }

  public static ImmutableList<String> nullSafeCopyOf(@Nullable String[] strings) {
    if (strings == null) {
      return ImmutableList.of();
    } else {
      return ImmutableList.copyOf(strings);
    }
  }

  @Nonnull
  public static ImmutableList<Integer> nullSafeCopyOf(@Nullable int[] ints) {
    if (ints == null) {
      return ImmutableList.of();
    } else {
      return ImmutableList.copyOf(Ints.asList(ints));
    }
  }

  public static String createNsString(List<Integer> asList) {
    return MediaWiki.pipeJoiner().join(asList);
  }

  public static String urlEncodedNamespace(ImmutableList<Integer> namespaces) {
    return MediaWiki.urlEncode(createNsString(namespaces));
  }

}
