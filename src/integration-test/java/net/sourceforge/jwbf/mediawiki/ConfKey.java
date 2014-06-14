package net.sourceforge.jwbf.mediawiki;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

public enum ConfKey {
  SITEINFO, INTERWIKI, SITENAME, MAINPAGE, //
  ALL_PAGE_CONT_2, ALL_PAGE_CONT_1, ALL_PAGE_2, ALL_PAGE_1, ALL_PAGE_0, //
  USERINFO_RIGHTS, USERINFO_GROUPS;

  private static final ImmutableMap<ConfKey, String> KEY_MAP =
      ImmutableMap.<ConfKey, String>builder() //
          .put(SITEINFO, "siteinfo") //
          .put(INTERWIKI, "interwiki") //
          .put(SITENAME, "sitename") //
          .put(MAINPAGE, "mainpage") //
          .put(ALL_PAGE_CONT_2, "allpage2continue") //
          .put(ALL_PAGE_CONT_1, "allpage1continue") //
          .put(ALL_PAGE_2, "allpage2") //
          .put(ALL_PAGE_1, "allpage1") //
          .put(ALL_PAGE_0, "allpage0") //
          .put(USERINFO_GROUPS, "userinfoGroups") //
          .put(USERINFO_RIGHTS, "userinfoRights") //
          .build() //
      ;

  public static String toString(ConfKey key) {
    return Preconditions.checkNotNull(KEY_MAP.get(key));
  }
}
