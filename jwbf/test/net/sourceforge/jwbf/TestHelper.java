package net.sourceforge.jwbf;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assume;

public abstract class TestHelper {

  private Random wheel = new Random();

  private static final TestHelper helper = new TestHelper() {
  };

  public TestHelper() {
    super();
  }

  public static String getRandomAlpha(int length) {
    return helper.getRandomAlph(length);
  }

  protected String getRandomAlph(int length) {
    return getRandom(length, 65, 90);
  }

  protected String getRandom(int length) {
    return getRandom(length, 48, 126);
  }

  protected String getRandom(int length, int begin, int end) {
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

  public static void prepareLogging() {
    File f = new File("test4log4j.properties");
    if (!f.exists()) {
      System.err.println("No logfile ! exit");
      System.exit(1);
    }
    PropertyConfigurator.configureAndWatch( f.getAbsolutePath(),
        60 * 1000);
  }

  public static void assumeReachable(URL host) {
    try {
      URLConnection c = host.openConnection();
      c.setConnectTimeout(2000);
      c.connect();

    } catch (Exception e) {
      Assume.assumeNoException(e);
    }
  }

  public static void assumeReachable(String url) throws MalformedURLException {
    assumeReachable(new URL(url));
  }

}