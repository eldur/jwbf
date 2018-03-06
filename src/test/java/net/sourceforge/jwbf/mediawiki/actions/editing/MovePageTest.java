package net.sourceforge.jwbf.mediawiki.actions.editing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;

import net.sourceforge.jwbf.core.actions.util.PermissionException;
import net.sourceforge.jwbf.core.contentRep.Userinfo;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

@RunWith(MockitoJUnitRunner.class)
public class MovePageTest {

  public static final ImmutableSet<String> VALID_MOVE_PERMISSIONS =
      ImmutableSet.of("move", "move-subpages");
  @Mock private MediaWikiBot bot;
  @Mock private Userinfo userinfo;

  private MovePage testee;

  @Before
  public void before() {
    when(bot.getUserinfo()).thenReturn(userinfo);
    Set<String> validPermissions = VALID_MOVE_PERMISSIONS;
    when(userinfo.getRights()).thenReturn(validPermissions);
    testee = new MovePage(bot, "old", "new", "why", true, false);
  }

  @Test
  public void testCheckPermissions() {
    // WHEN
    testee.checkPermissions(VALID_MOVE_PERMISSIONS, true);
    // THEN
    // no exception

  }

  @Test
  public void testCheckPermissions_withSubPages() {
    // GIVEN
    boolean subPages = true;
    Set<String> permissions = ImmutableSet.of("move");

    // WHEN
    try {
      testee.checkPermissions(permissions, subPages);
      fail();
    } catch (PermissionException e) {
      // THEN
      assertEquals(
          "The given user doesn't have the rights to move subpages. "
              + "Add '$wgGroupPermissions['bot']['move-subpages'] = true;' to your MediaWikis "
              + "LocalSettings.php might solve this problem.",
          e.getMessage());
    }
  }

  @Test
  public void testCheckPermissions_noSubPages() {
    // GIVEN
    boolean subPages = false;
    Set<String> permissions = ImmutableSet.of();

    // WHEN
    try {
      testee.checkPermissions(permissions, subPages);
      fail();
    } catch (PermissionException e) {
      // THEN
      assertEquals(
          "The given user doesn't have the rights to move. "
              + "Add '$wgGroupPermissions['bot']['move'] = true;' to your MediaWikis "
              + "LocalSettings.php might solve this problem.",
          e.getMessage());
    }
  }
}
