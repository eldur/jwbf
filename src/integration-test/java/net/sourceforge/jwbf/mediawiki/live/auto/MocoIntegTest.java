package net.sourceforge.jwbf.mediawiki.live.auto;

import static com.github.dreamhead.moco.Moco.httpserver;
import static com.github.dreamhead.moco.Runner.runner;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Provider;

import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.mediawiki.BotFactory;
import net.sourceforge.jwbf.mediawiki.ConfKey;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.io.Resources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@RunWith(Parameterized.class)
public abstract class MocoIntegTest implements Provider<MediaWikiBot> {

  private final Version version;
  private Runner runner;
  private int port;
  protected HttpServer server;
  private MediaWikiBot bot;

  private final Config conf = ConfigFactory.load("mediawiki.conf");

  public MocoIntegTest(Version version) {
    this.version = version;
  }

  /**
   * @deprecated
   */
  @Deprecated
  protected void before(boolean login) {
    bot(BotFactory.getIntegMediaWikiBot(version(), login));
  }

  protected Config conf() {
    String numberVariation = version().getNumberVariation();
    if (conf.hasPath(numberVariation)) {
      return conf.getConfig(numberVariation).withFallback(conf);
    }
    return conf;
  }

  protected String confOf(ConfKey key) {
    return conf().getString(ConfKey.toString(key));
  }

  public MediaWikiBot bot() {
    return bot;
  }

  @Override
  public MediaWikiBot get() {
    return bot();
  }

  public static Collection<?> prepare(Version... versions) {
    Object[][] objects = new Object[versions.length][1];
    for (int i = 0; i < versions.length; i++) {
      objects[i][0] = versions[i];
    }

    return Arrays.asList(objects);
  }

  @Before
  public void setup() {
    server = httpserver(); // Moco.log()
    runner = runner(server);
    runner.start();
    port = server.port();
    bot(new MediaWikiBot(host()));
  }

  protected void bot(MediaWikiBot bot) {
    this.bot = bot;

  }

  protected ContentResource mwFileOf(MediaWiki.Version version, String filename) {
    String number = version.getNumber().replace(".", "-");
    String prefix = "mediawiki/v" + number + "/";
    File file = fileOf(prefix + filename);
    if (file.canRead()) {
      String absFileName = file.getAbsolutePath();
      return Moco.file(absFileName);
    } else {
      return Moco.text("SHOULD FAIL");
    }
  }

  protected File fileOf(String filename) {
    try {
      return JWBF.urlToFile(Resources.getResource(filename));
    } catch (IllegalArgumentException e) {
      return new File(filename);
    }
  }

  protected String host() {
    return "http://localhost:" + port + "/";
  }

  public Version version() {
    return version;
  }

  @After
  public void tearDown() {
    runner.stop();
  }

  public ImmutableMap<String, String> emptyStringMap() {
    return ImmutableMap.<String, String> of();
  }

  protected ImmutableList<String> splittedConfigOfString(ConfKey key,
      final ImmutableMap<String, String> replacements) {
    Iterable<String> splitted = Splitter.on(",") //
        .trimResults() //
        .omitEmptyStrings() //
        .split(confOf(key));

    return FluentIterable.from(splitted) //
        .transform(new Function<String, String>() {

          @Override
          public String apply(String input) {

            for (Entry<String, String> entry : replacements.entrySet()) {
              String key = String.format("${%s}", entry.getKey());
              if (input.contains(key)) {
                return input.replaceFirst(String.format("\\$\\{%s\\}", entry.getKey()),
                    entry.getValue());
              }
            }
            return input;
          }
        }).toList();
  }

  protected ImmutableList<String> splittedConfigOfString(ConfKey key) {
    return splittedConfigOfString(key, ImmutableMap.<String, String> of());
  }

  private static final Function<Entry<? extends Object, String>, String> function = new Function<Map.Entry<? extends Object, String>, String>() {

    @Override
    public String apply(Entry<? extends Object, String> input) {
      Entry<? extends Object, String> in = Preconditions.checkNotNull(input);
      return in.getKey() + "=" + in.getValue();
    }
  };

  protected ImmutableList<String> toSortedList(Map<? extends Object, String> stringList) {
    return FluentIterable.from(stringList.entrySet()) //
        .transform(function) //
        .toSortedList(Ordering.natural()) //
    ;
  }
}
