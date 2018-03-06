package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Optional;

public class OptionalsTest {

  @Test
  public void testAbsentIfEmpty() {
    assertEquals(Optional.<String>absent(), Optionals.absentIfEmpty(null));
    assertEquals(Optional.<String>absent(), Optionals.absentIfEmpty(""));
    assertEquals(Optional.<String>absent(), Optionals.absentIfEmpty(" "));
    assertEquals(Optional.<String>absent(), Optionals.absentIfEmpty("\t"));
    assertEquals(Optional.of("g"), Optionals.absentIfEmpty("g"));
  }
}
