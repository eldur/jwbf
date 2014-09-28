package net.sourceforge.jwbf.mediawiki;

import com.google.common.collect.ImmutableMap;
import net.sourceforge.jwbf.core.internal.Checked;

public enum ConfKey {
  SITEINFO, INTERWIKI, SITENAME, MAINPAGE, //
  ALL_PAGE_CONT_2, ALL_PAGE_CONT_1, ALL_PAGE_2, ALL_PAGE_1, ALL_PAGE_0, //
  USERINFO_RIGHTS, USERINFO_GROUPS, //
  BACKLINKS_PAGES, BACKLINKS_CONT_2, BACKLINKS_CONT_1;

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
          .put(BACKLINKS_PAGES, "backlinks") //
          .put(BACKLINKS_CONT_1, "backlinks1continue") //
          .put(BACKLINKS_CONT_2, "backlinks2continue") //
          .put(USERINFO_GROUPS, "userinfoGroups") //
          .put(USERINFO_RIGHTS, "userinfoRights") //
          .build() //
      ;

  public static String toString(ConfKey key) {
    return Checked.nonNull(KEY_MAP.get(key), "value for key: " + key);
  }
}
