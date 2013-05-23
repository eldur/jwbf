package net.sourceforge.jwbf;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assume;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Slf4j
public class TestHelper {

  private static Random wheel = new Random();

  private TestHelper() {
  }

  public static String getRandomAlpha(int length) {
    return getRandomAlph(length);
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

  private static LoadingCache<URL, Boolean> reachableCache = CacheBuilder.newBuilder() //
      .expireAfterAccess(30, TimeUnit.SECONDS) //
      .build(new CacheLoader<URL, Boolean>() {

        @Override
        public Boolean load(URL url) throws Exception {
          try {
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setConnectTimeout(2000);
            c.connect();
            String headerField = c.getHeaderField(0);
            return headerField.endsWith("200 OK");

          } catch (Exception e) {
            log.warn(e.getMessage());
            log.trace("", e);

          }
          return Boolean.FALSE;
        }
      });

  public static void assumeReachable(URL url) {
    try {
      Boolean reachable = reachableCache.get(url);
      Assume.assumeTrue(reachable.booleanValue());
    } catch (ExecutionException e1) {
      throw new IllegalStateException(e1);
    }

  }

  public static void assumeReachable(String url) {
    try {
      Assume.assumeFalse(url.trim().isEmpty());
      assumeReachable(new URL(url));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
