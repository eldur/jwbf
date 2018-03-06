package net.sourceforge.jwbf.mediawiki;

import static org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.inject.Provider;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.resource.ContentResource;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.io.Resources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;

import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.JWBF;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import net.sourceforge.jwbf.mediawiki.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.meta.SiteInfoIntegTest;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import net.sourceforge.jwbf.mediawiki.live.auto.ParamHelper;

@RunWith(Parameterized.class)
public abstract class MocoIntegTest extends AbstractIntegTest implements Provider<MediaWikiBot> {

  public static final String MEDIAWIKI_CONF = "mediawiki.conf";
  private final Version version;
  private MediaWikiBot bot;

  private final Config conf = ConfigFactory.load(MEDIAWIKI_CONF);

  public MocoIntegTest(Version version) {
    this.version = version;
  }

  @Parameters(name = "{0}")
  public static Collection<?> stableWikis() {
    return ParamHelper.prepare(Version.valuesStable());
  }

  protected void livebot() {
    livebot(true);
  }

  protected void livebot(boolean login) {
    bot(BotFactory.getIntegMediaWikiBot(version(), login));
  }

  /** @deprecated */
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
    String confKey = ConfKey.toString(key);
    try {
      return conf().getString(confKey);
    } catch (ConfigException.Missing e) {
      String message = "check \"" + confKey + "\" in test/resources/" + MEDIAWIKI_CONF;
      throw new IllegalStateException(message, e);
    }
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
    bot(new MediaWikiBot(host()));
  }

  protected void bot(MediaWikiBot bot) {
    this.bot = bot;
  }

  public static void applySiteinfoXmlToServer(HttpServer server, Version latest, Class<?> clazz) {
    applyToServer(
        server,
        SiteInfoIntegTest.newSiteInfoMatcherBuilder(),
        "siteinfo_detail.xml",
        latest,
        clazz);
  }

  public static void applyToServer(
      HttpServer server,
      ApiMatcherBuilder apiMatcherBuilder,
      String filename,
      Version version,
      Class<?> clazz) {
    server
        .request(apiMatcherBuilder.build()) //
        .response(mwFileOfInner(version, filename, clazz));
  }

  protected void applySiteinfoXmlToServer() {
    applySiteinfoXmlToServer(server, version(), this.getClass());
  }

  protected void applyToServer(ApiMatcherBuilder apiMatcherBuilder, String filename) {
    MocoIntegTest.applyToServer(server, apiMatcherBuilder, filename, version(), this.getClass());
  }

  private static ContentResource mwFileOfInner(
      MediaWiki.Version version, String filename, Class<?> clazz) {
    File file = getFileInner(TestHelper.mediaWikiFileName(version, filename));
    if (file.canRead()) {
      String absFileName = file.getAbsolutePath();
      return Moco.file(absFileName);
    } else {
      return Moco.text(clazz.getCanonicalName() + " : SHOULD FAIL : File not found: " + filename);
    }
  }

  protected ContentResource mwFileOf(MediaWiki.Version version, String filename) {
    return mwFileOfInner(version, filename, this.getClass());
  }

  protected File fileOf(String filename) {
    return getFileInner(filename);
  }

  private static File getFileInner(String filename) {
    try {
      return JWBF.urlToFile(Resources.getResource(filename));
    } catch (IllegalArgumentException e) {
      return new File(filename);
    }
  }

  public Version version() {
    return version;
  }

  public static ImmutableMap<String, String> emptyStringMap() {
    return ImmutableMap.of();
  }

  protected ImmutableList<String> splittedConfigOfString(
      ConfKey key, final ImmutableMap<String, String> replacements) {
    Iterable<String> splitted =
        Splitter.on(",") //
            .trimResults() //
            .omitEmptyStrings() //
            .split(confOf(key));

    return FluentIterable.from(splitted) //
        .transform(
            new Function<String, String>() {

              @Override
              public String apply(String input) {

                for (Entry<String, String> entry : replacements.entrySet()) {
                  String key = String.format("${%s}", entry.getKey());
                  if (input.contains(key)) {
                    return input.replaceFirst(
                        String.format("\\$\\{%s\\}", entry.getKey()), entry.getValue());
                  }
                }
                return input;
              }
            })
        .toList();
  }

  protected ImmutableList<String> splittedConfigOfString(ConfKey key) {
    return splittedConfigOfString(key, ImmutableMap.<String, String>of());
  }

  private static Function<Entry<? extends Object, String>, String> function =
      new NonnullFunction<Entry<? extends Object, String>, String>() {

        @Nonnull
        @Override
        public String applyNonnull(@Nonnull Entry<? extends Object, String> in) {
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
