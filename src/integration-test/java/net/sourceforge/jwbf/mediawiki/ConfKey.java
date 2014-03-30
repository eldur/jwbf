package net.sourceforge.jwbf.mediawiki;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public enum ConfKey {
  SITEINFO, INTERWIKI, SITENAME, MAINPAGE;

  private static final ImmutableMap<ConfKey, String> KEY_MAP = ImmutableMap
      .<ConfKey, String> builder() //
      .put(SITEINFO, "siteinfo") //
      .put(INTERWIKI, "interwiki") //
      .put(SITENAME, "sitename") //
      .put(MAINPAGE, "mainpage") //
      .build() //
  ;

  public static String toString(ConfKey key) {
    return Preconditions.checkNotNull(KEY_MAP.get(key));
  }
}
