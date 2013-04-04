package net.sourceforge.jwbf.mediawiki;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.inject.Provider;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki.Version;
import net.sourceforge.jwbf.mediawiki.actions.util.MWAction;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.rules.Verifier;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Slf4j
public class VersionTestClassVerifier extends Verifier {

  private final Class<?>[] clazz;

  private boolean isVersionTestCase = false;

  private final Map<String, Version> testedVersions = Maps.newHashMap();
  private final Map<String, Version> documentedVersions = Maps.newHashMap();

  private boolean checkAll = true;

  private List<Version> ignoredVersions = Lists.newArrayList();

  public VersionTestClassVerifier(Class<?>... clazz) {
    this.clazz = clazz;
  }

  @Override
  public Statement apply(Statement base, Description description) {
    for (Class<?> c : clazz) {

      addInitSupporterVersions(c);
    }
    return super.apply(base, description);
  }

  private void addInitSupporterVersions(Class<?> mwc) {
    isVersionTestCase = true;
    Version[] vs = MWAction.findSupportedVersions(mwc);
    for (int j = 0; j < vs.length; j++) {
      documentedVersions.put(mwc.getCanonicalName() + "-" + vs[j], vs[j]);
    }

  }

  private Collection<Version> getUsedVersions() {

    assumeNoIgnoredVersions();
    Version[] stableVersions = Version.valuesStable();
    Iterable<Version> versions = Lists.newArrayList(stableVersions);

    final Set<Version> testedKeys = Sets.newHashSet(testedVersions.values());

    versions = Iterables.filter(versions, new Predicate<Version>() {

      public boolean apply(@Nullable Version version) {
        return !testedKeys.contains(version);
      }
    });

    return Lists.newArrayList(versions);
  }

  private Map<String, Version> getTestedButUndocmentedVersions() {
    final Map<String, Version> data = Maps.newHashMap();
    data.putAll(testedVersions);

    final Set<String> documentedKeys = documentedVersions.keySet();
    for (String key : documentedKeys) {
      data.remove(key);
    }
    return data;
  }

  /**
   * Use in a valid testcase.
   * 
   */
  private void registerTestedVersion(Class<?> clazz, Version v) {
    if (v != Version.DEVELOPMENT) {
      testedVersions.put(clazz.getCanonicalName() + "-" + v, v);
    }
  }

  private void assertAll() {
    if (isVersionTestCase) {

      Assert.assertFalse(
          "no versions are supported; check annotations " + documentedVersions.toString(),
          documentedVersions.values().contains(Version.UNKNOWN) && documentedVersions.size() == 1);
      assertDocumentedTests();
      assertAllTestedVersionsAreDocumented();

      if (checkAll) {
        assertEquals("missing tests for versions.", "[]", fmt(getUsedVersions()));
      }
    }
  }

  private String fmt(Collection<Version> usedVersions2) {
    return usedVersions2.toString();
  }

  private void assertAllTestedVersionsAreDocumented() {
    assertEquals("There are undocumented tests for versions. ", "",
        fmt(getTestedButUndocmentedVersions().entrySet()));
  }

  private void assertDocumentedTests() {
    Map<String, Version> testedAndDocumentedVersions = Maps.newHashMap();
    for (Entry<String, Version> entrySet : documentedVersions.entrySet()) {
      String key = entrySet.getKey();
      Version version = testedVersions.get(key);
      if (version != null) {
        testedAndDocumentedVersions.put(key, version);
      }
    }
    assumeNoIgnoredVersions();
    assertEquals("not all documented versions are tested ", fmt(documentedVersions.entrySet()),
        fmt(testedAndDocumentedVersions.entrySet()));
  }

  private void assumeNoIgnoredVersions() {
    Assume.assumeTrue(ignoredVersions.isEmpty());
  }

  private String fmt(Iterable<Entry<String, Version>> elements) {
    StringBuilder sb = new StringBuilder();
    List<Entry<String, Version>> entrySet = Lists.newArrayList(elements);
    Collections.sort(entrySet, new Comparator<Entry<String, Version>>() {

      public int compare(Entry<String, Version> o1, Entry<String, Version> o2) {
        return o1.getKey().compareTo(o2.getKey());
      }

    });
    for (Entry<String, Version> entry : entrySet) {
      sb.append(shortForm(entry.getKey()));
      sb.append(" : ");
      sb.append(entry.getValue());
      sb.append("\n");
    }

    return sb.toString();
  }

  private String shortForm(String key) {
    int length = key.length();
    if (length > 20) {
      Pattern p1 = Pattern.compile("[^\\.]+");
      Matcher m1 = p1.matcher(key);
      String last = "";
      StringBuffer sb = new StringBuffer();
      while (m1.find()) {
        last = m1.group();
        m1.appendReplacement(sb, m1.group().charAt(0) + "");
      }
      m1.appendTail(sb);
      return sb.toString().replaceFirst("[\\.].$", "." + last);

    }
    return key;
  }

  @Override
  protected void verify() throws Throwable {
    assertAll();
  }

  public Verifier getSuccessRegister(final Provider<MediaWikiBot> botProvider) {
    return new Verifier() {

      @Override
      protected void verify() throws Throwable {
        MediaWikiBot bot = botProvider.get();
        if (bot != null) {
          for (Class<?> c : clazz) {
            try {
              registerTestedVersion(c, bot.getVersion());
            } catch (RuntimeException e) {
              log.error("", e);
            }
          }
        }
      }
    };
  }

  public VersionTestClassVerifier dontCheckAll() {
    checkAll = false;
    return this;
  }

  public void addIgnoredVersion(Version v) {
    ignoredVersions.add(v);

  }

}
