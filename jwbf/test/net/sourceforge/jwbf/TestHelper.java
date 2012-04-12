package net.sourceforge.jwbf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.junit.Assume;

public abstract class TestHelper {

  private static Random wheel = new Random();

  private static final TestHelper helper = new TestHelper() {
  };

  public TestHelper() {
    super();
  }

  public static String getRandomAlpha(int length) {
    return helper.getRandomAlph(length);
  }

  public static String getRandomAlph(int length) {
    return getRandom(length, 65, 90);
  }

  public static String getRandom(int length) {
    return getRandom(length, 48, 126);
  }

  public static String getRandom(int length, int begin, int end) {
    StringBuffer out = new StringBuffer();
    int charNum = 0;
    int count = 1;
    while (count <= length) {
      charNum = (wheel.nextInt(79) + begin);
      if (charNum >= begin && charNum <= end) {

        char d = (char) charNum;
        out.append(d);
        count++;
      }
    }
    return out.toString();
  }

  /**
   * @deprecated do not use log4j
   */
  @Deprecated
  public static void prepareLogging() {
    File f = new File("test4log4j.properties");
    if (!f.exists()) {
      System.err.println("No logfile ! exit");
      System.exit(1);
    }
  }

  public static void assumeReachable(URL url) {
    try {
      URLConnection c = url.openConnection();
      c.setConnectTimeout(2000);
      c.connect();

    } catch (Exception e) {
      Assume.assumeNoException(e);
    }
  }

  public static void assumeReachable(String url) {
    try {
      assumeReachable(new URL(url));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void assumeLiveTestEnvoirnmentReachable() {
    // TODO test
    Assume.assumeNoException(new RuntimeException("envoirnment not reachable"));
  }

}