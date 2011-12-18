package net.sourceforge.jwbf.zim.live;

import java.io.File;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.TestHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class BasicTest {

  @BeforeClass
  public static void setUp() throws Exception {
    TestHelper.prepareLogging();
    File dir = new File("zimTest");
    dir.mkdir();
    if (!(dir.isDirectory() && dir.exists())) {
      throw new Exception("no testdir");
    }
    //		bots.add(new ZimWikiBot(dir));
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
