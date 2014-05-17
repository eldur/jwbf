package net.sourceforge.jwbf.zim.live;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicTest {

  private static final Logger log = LoggerFactory.getLogger(BasicTest.class);

  @BeforeClass
  public static void setUp() throws Exception {
    File dir = new File("zimTest");
    dir.mkdir();
    if (!(dir.isDirectory() && dir.exists())) {
      throw new Exception("no testdir");
    }
    // bots.add(new ZimWikiBot(dir));
    dir.delete();
  }

  @Before
  public void doNothing() {

  }

  @Test
  public void doLog() {
    log.info("Hello");
  }

}
