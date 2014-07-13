package net.sourceforge.jwbf;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.google.common.io.Resources;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import org.junit.Assume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHelper {

  private static final Logger log = LoggerFactory.getLogger(TestHelper.class);

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
    StringBuilder out = new StringBuilder();
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

  public static void assumeReachable(String... urls) {
    for (String url : urls) {
      assumeReachable(url);
    }
  }

  public static void assumeReachable(String url) {
    Assume.assumeFalse(url.trim().isEmpty());
    assumeReachable(JWBF.newURL(url));
  }

  public static String mediaWikiFileName(MediaWiki.Version version, String filename) {
    String number = version.getNumber().replace(".", "-");
    String prefix = "mediawiki/v" + number + "/";
    return prefix + filename;
  }

  public static String wikiResponse(MediaWiki.Version version, String filename) {
    return textOf(mediaWikiFileName(version, filename));
  }

  public static String anyWikiResponse(String filename) {
    return textOf("mediawiki/any/" + filename);
  }

  private static String textOf(String filename) {
    try {
      return Resources.toString(Resources.getResource(filename), Charsets.UTF_8);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static ImmutableList<String> createNames(final String prefix, int limit) {
    Range<Integer> range = Range.closedOpen(0, limit);
    ImmutableList<Integer> list = ContiguousSet.create(range, DiscreteDomain.integers()).asList();
    return FluentIterable.from(list).transform(new Function<Integer, String>() {
      @Nullable
      @Override
      public String apply(@Nullable Integer input) {
        return prefix + input;
      }
    }).toList();
  }

}
