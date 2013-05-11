package net.sourceforge.jwbf.mediawiki.actions.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Slf4j
public class MWActionTest {

  private Function<? super Version, String> toString = new Function<Version, String>() {

    public String apply(Version input) {
      return input.getNumber();
    }
  };
  private MWAction action;

  @Before
  public void before() {
    action = Mockito.mock(MWAction.class, Mockito.CALLS_REAL_METHODS);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testSetExceptionHandler() {
    try {
      MWAction.setExceptionHandler(new ExceptionHandler() {
        public void handle(RuntimeException e) {
          log.warn(e.getMessage());
        }
      });
      Mockito.when(action.getSupportedVersions()).thenReturn(ImmutableList.<Version> of());
      action.checkVersionNewerEquals(Version.DEVELOPMENT);
    } finally {
      MWAction.setExceptionHandler(MWAction.DEFAULT_EXCEPTION_HANDLER);
    }
  }

  @Test
  public void testCheckVersionNewerEqualsEmpty() {
    Mockito.when(action.getSupportedVersions()).thenReturn(ImmutableList.<Version> of());
    try {
      action.checkVersionNewerEquals(Version.DEVELOPMENT);
      fail();
    } catch (VersionException e) {
      assertEquals("unsupported version: DEVELOPMENT", e.getMessage());
    }
  }

  @Test
  public void testCheckVersionNewerEquals() {
    Mockito.when(action.getSupportedVersions()).thenReturn(ImmutableList.of(Version.MW1_18));
    Collection<Version> supportedVersions = action.getSupportedVersions();
    assertEquals(ImmutableList.of("1.18").toString(),
        Lists.transform(Lists.newArrayList(supportedVersions), toString).toString());

    action.checkVersionNewerEquals(Version.DEVELOPMENT);
    action.checkVersionNewerEquals(Version.MW1_19);
    action.checkVersionNewerEquals(Version.MW1_18);

    try {
      action.checkVersionNewerEquals(Version.MW1_17);
      fail();
    } catch (VersionException e) {
      assertEquals("unsupported version: MW1_17", e.getMessage());
    }

    try {
      action.checkVersionNewerEquals(Version.UNKNOWN);
      fail();
    } catch (VersionException e) {
      assertEquals("unsupported version: UNKNOWN", e.getMessage());
    }
  }
}
