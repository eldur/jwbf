package net.sourceforge.jwbf;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.sourceforge.jwbf.JWBF.ContainerEntry;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static org.junit.Assert.*;

public class JWBFTest {

  @Before
  public void before() {
    JWBF.cache = null;
  }

  @Test
  public void testInit_invalidFile() {
    // GIVEN
    URL url = JWBF.newURL("file:///a");
    String packageName = "";

    // WHEN
    Map<String, String> result = JWBF.init(packageName, JWBF.toUri(url));

    // THEN
    Map<String, String> expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_bundle() {
    // GIVEN
    URL url = JWBF.newURLWithoutHandler("bundle:///a");
    String packageName = "";

    // WHEN
    Map<String, String> result = JWBF.init(packageName, JWBF.toUri(url));

    // THEN
    Map<String, String> expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_whatEver() {
    // GIVEN
    URL url = JWBF.newURLWithoutHandler("whatEver:///a");
    String packageName = "";

    // WHEN
    Map<String, String> result = JWBF.init(packageName, JWBF.toUri(url));

    // THEN
    Map<String, String> expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_jar_notExisting() {
    // GIVEN
    URL url = JWBF.newURL("jar:file:/home/noOne/lib/jwbf.jar!/net/sourceforge/jwbf");
    String packageName = "net/sourceforge/jwbf";

    // WHEN
    Map<String, String> result = JWBF.init(packageName, JWBF.toUri(url));

    // THEN
    Map<String, String> expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_realFile() {
    // GIVEN
    Path targetDir = newMockFileSystem();
    Path tempDirectory = new NioUnchecked().createTempDir(targetDir, "manifest-test");
    Path manifestFile = tempDirectory.resolveSibling("MANIFEST.MF");
    String expectedVersion = "999.0.0-SNAPSHOT-${buildNumber}";
    String expectedTitle = "jwbf";
    byte[] bytes =
        Joiner.on("\n")
            .join(
                "Manifest-Version: 1.0", //
                "Implementation-Title: " + expectedTitle, //
                "Implementation-Version: " + expectedVersion,
                "") //
            .getBytes();
    new NioUnchecked().writeBytes(manifestFile, bytes);
    // ---

    String name = MediaWikiBot.class.getPackage().getName();
    ImmutableList<String> fileNames = ImmutableList.copyOf(Splitter.on(".").split(name));

    String firstFileName = Iterables.getFirst(fileNames, null);
    Path first = tempDirectory.resolve(firstFileName);

    for (String fileName : Iterables.skip(fileNames, 1)) {
      first = first.resolve(fileName);
    }
    new NioUnchecked().createDirectories(first);

    String packageName = name.replace(".", "/");

    // WHEN
    Map<String, String> result = JWBF.init(packageName, tempDirectory.toUri());

    // THEN
    Map<String, String> expected =
        ImmutableMap.<String, String>builder() //
            .put(JWBF.DEVEL_NAME + "-mediawiki", JWBF.DEVEL_VERSION) //
            .build();
    assertEquals(expected, result);
  }

  private Path getTargetDir() {
    Path targetDir = Paths.get("target");
    if (!(Files.exists(targetDir) && Files.isDirectory(targetDir))) {
      fail("no target dir found");
    }
    return targetDir;
  }

  private Path newMockFileSystem() {
    FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
    Path foo = fs.getPath("/foo");
    return new NioUnchecked().createDirectory(foo);
  }

  @Test
  public void testSecondIfNull() {
    assertEquals("a", JWBF.secondIfNull(null, "a"));
    assertEquals("b", JWBF.secondIfNull("b", "a"));
    assertEquals(null, JWBF.secondIfNull(null, null));
    assertEquals("b", JWBF.secondIfNull("b", null));
  }

  @Test
  public void testGetVersions() {
    // GIVEN
    JWBF.cache =
        ImmutableMap.<String, String>builder() //
            .put("jwbf-generic-mediawiki", JWBF.DEVEL_VERSION) //
            .build();

    // WHEN
    String version = JWBF.getVersion(MediaWikiBot.class);

    // THEN
    assertEquals(JWBF.DEVEL_VERSION, version);
  }

  @Test
  public void testGetPartId() {
    // GIVEN
    JWBF.cache =
        ImmutableMap.<String, String>builder() //
            .put("jwbf-generic-mediawiki", "anyVersion") //
            .build();

    // WHEN
    String partId = JWBF.getPartId(MediaWikiBot.class);

    // THEN
    assertEquals("jwbf-generic-mediawiki", partId);
  }

  @Test
  public void testGetPartId_core() {
    // GIVEN
    JWBF.cache =
        ImmutableMap.<String, String>builder() //
            .put("jwbf-generic-core", "anyVersion") //
            .build();
    // WHEN
    String partId = JWBF.getPartId(HttpActionClient.class);

    // THEN
    assertEquals("jwbf-generic-core", partId);
  }

  @Test
  public void testMakeVersionMap() {
    // GIVEN
    List<ContainerEntry> elements =
        ImmutableList.of( //
            new ContainerEntry("META-INF/", true), //
            new ContainerEntry("META-INF/MANIFEST.MF", false), //
            new ContainerEntry("net/", true), //
            new ContainerEntry("net/sourceforge/", true), //
            new ContainerEntry("net/sourceforge/jwbf/", true), //
            new ContainerEntry("net/sourceforge/jwbf/trac/", true), //
            new ContainerEntry("net/sourceforge/jwbf/trac/actions/", true), //
            new ContainerEntry("net/sourceforge/jwbf/mediawiki/", true), //
            new ContainerEntry("net/sourceforge/jwbf/mediawiki/actions/", true), //
            new ContainerEntry("net/sourceforge/jwbf/trac/actions/GetRevision.class", false), //
            new ContainerEntry(
                "net/sourceforge/jwbf/mediawiki/actions/queries/BacklinkTitles$1.class", false) //
            );
    String packageName = "net/sourceforge/jwbf";

    // WHEN
    Map<String, String> result =
        JWBF.makeVersionMap(packageName, elements, "DEVEL", "jwbf-generic");

    // THEN
    Map<String, String> expected =
        ImmutableMap.<String, String>builder() //
            .put("jwbf-generic-mediawiki", "DEVEL") //
            .put("jwbf-generic-trac", "DEVEL") //
            .build();
    assertEquals(expected, result);
  }

  @Test
  public final void showVersions() {
    assertNotNull(JWBF.getVersions());
    JWBF.printVersion();
    JWBF.main(null);
  }

  @Test
  public void testGetVersions_unknown() {

    // WHEN
    String jwbfVersion = JWBF.getVersion(this.getClass());

    // THEN
    assertEquals("Version unknown", jwbfVersion);
  }

  @Test
  public void testJarManifestLookup() {
    // GIVEN
    URI mockUri = newTestJar().toUri();

    // WHEN
    Map<String, String> actual = JWBF.jarVersionDetails("test", mockUri);

    // THEN
    GAssert.assertEquals(ImmutableMap.of(JWBF.DEVEL_NAME + "-first", JWBF.DEVEL_VERSION), actual);
  }

  @Test
  public void testRemoveTrailingSlash() {
    assertEquals("b", JWBF.removeTrailingSlash("b"));
    assertEquals(" / ", JWBF.removeTrailingSlash(" / "));
    assertEquals("/", JWBF.removeTrailingSlash("/"));
    assertEquals(" ", JWBF.removeTrailingSlash(" "));
    assertEquals("//", JWBF.removeTrailingSlash("//"));
    assertEquals("/a/.", JWBF.removeTrailingSlash("/a/./"));
    assertEquals("/a/.-", JWBF.removeTrailingSlash("/a/.-/"));
  }

  @Test
  public void testCountSlashes() {
    assertEquals(0, JWBF.countSlashes("b"));
    assertEquals(1, JWBF.countSlashes(" / "));
    assertEquals(2, JWBF.countSlashes("//"));
    assertEquals(3, JWBF.countSlashes("/a/./"));
    assertEquals(3, JWBF.countSlashes("/a/.-/"));
  }

  @Test
  public void testJarToEntriesEmpty()  {
    JWBF.jarToEntries(URI.create("file:://invalid"));
  }

  @Test
  public void testJarToEntries() {

    // GIVEN
    Path jarFile = newTestJar();

    // WHEN
    List<ContainerEntry> result = JWBF.jarToEntries(jarFile.toUri());

    Function<ContainerEntry, ContainerEntry> function =
        new Function<ContainerEntry, ContainerEntry>() {

          @Override
          public ContainerEntry apply(ContainerEntry input) {
            if (input == null) {
              return null;
            }
            return new ContainerEntry(
                input.getName().replaceAll("[0-9]+", ""), input.isDirectory());
          }
        };
    // THEN
    ImmutableList<ContainerEntry> mutated =
        Ordering.usingToString().immutableSortedCopy(Lists.transform(result, function));

    ImmutableList<ContainerEntry> expected =
        ImmutableList.<ContainerEntry>builder() //
            .add(new ContainerEntry("META-INF/MANIFEST.MF", false)) //
            .add(new ContainerEntry("target/jarfile-test/jarContent/", true)) //
            .add(new ContainerEntry("target/jarfile-test/jarContent/first/", true)) //
            .add(new ContainerEntry("target/jarfile-test/jarContent/first/second/", true)) //
            .build();
    GAssert.assertEquals(expected, mutated);
  }

  synchronized Path newTestJar() {
    Path targetDir = getTargetDir();
    Path tempDirectory = new NioUnchecked().createTempDir(targetDir, "jarfile-test");
    Path jarFile = tempDirectory.resolveSibling("a.jar");

    if (!Files.exists(jarFile)) {
      Path inputDir = new NioUnchecked().createDirectory(tempDirectory, "jarContent");
      Path firstDir = new NioUnchecked().createDirectory(inputDir, "first");
      new NioUnchecked().createDirectory(firstDir, "second");
      newJarFile(jarFile, inputDir);
    }
    return jarFile;
  }

  public void newJarFile(Path jarFile, Path inputDir) {
    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

    try (JarOutputStream target = newJarOutputStream(jarFile, manifest)) {
      addRecursiv(inputDir, target);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private JarOutputStream newJarOutputStream(Path jarFile, Manifest manifest) {
    try {
      return new JarOutputStream(new NioUnchecked().newOutputStream(jarFile), manifest);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private void addRecursiv(Path source, JarOutputStream target) {
    if (Files.isDirectory(source)) {
      JarEntry entry = makeEntry(source);
      putAndClose(entry, target);
      for (Path nestedFile : new NioUnchecked().listFiles(source)) {
        addRecursiv(nestedFile, target);
      }
      return;
    }

    addFile(source, target);
  }

  private void putAndClose(JarEntry entry, JarOutputStream target) {
    try {
      target.putNextEntry(entry);
      target.closeEntry();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private void addFile(Path source, JarOutputStream target) {
    try {
      JarEntry entry = makeEntry(source);
      target.putNextEntry(entry);

      try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(source)); ) {

        byte[] buffer = new byte[1024];
        while (true) {
          int count = in.read(buffer);
          if (count == -1) {
            break;
          }
          target.write(buffer, 0, count);
        }
        target.closeEntry();
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private JarEntry makeEntry(Path source) {

    String name = source.toString().replace("\\", "/");
    if (!name.isEmpty()) {
      if (!name.endsWith("/")) {
        name += "/";
      }
    }
    JarEntry entry = new JarEntry(name);
    entry.setTime(new NioUnchecked().getLastModifiedTime(source).toMillis());
    return entry;
  }

  @Test
  public void testUrlToFile() throws Exception {
    // GIVEN
    File file = new File(".").getAbsoluteFile();
    URL url = file.toURI().toURL();

    // WHEN
    File urlToFile = JWBF.urlToFile(url);

    // THEN
    assertEquals(file, urlToFile);
  }

  @Test
  public void testToUri() {
    try {
      // GIVEN/WHEN
      JWBF.toUri("\\invalid");
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("\\invalid", e.getMessage());
    }
  }

  @Test
  public void testNewURL() {
    try {
      // GIVEN/WHEN
      JWBF.newURL("\\invalid");
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("\"\\invalid\"", e.getMessage());
    }
  }

  @Test
  public void testStripJarSuffix() {

    URI uri = JWBF.toUri("jar:file:/any/target/jwbf-3.0.0-snapshot.jar!/net/sourceforge/jwbf");
    String actual = JWBF.stripJarSuffix(uri).toString();

    assertEquals("file:/any/target/jwbf-3.0.0-snapshot.jar", actual);
  }

  @Test
  public void testStripJarSuffix_file() {

    URI uri = JWBF.toUri("file:/any/target/jwbf-3.0.0-snapshot.jar");
    String actual = JWBF.stripJarSuffix(uri).toString();

    assertEquals("file:/any/target/jwbf-3.0.0-snapshot.jar", actual);
  }

  @Test(expected = NullPointerException.class)
  public void testStripJarSuffix_null() {
    JWBF.stripJarSuffix(null);
    fail();
  }

  @Test
  public void testNewURLWithoutHandler() {
    try {
      // GIVEN/WHEN
      JWBF.newURLWithoutHandler("\\invalid");
      fail();
    } catch (IllegalArgumentException e) {
      // THEN
      assertEquals("\\invalid", e.getMessage());
    }
  }

  @Test
  public void testNewURLWithoutHandler_open() {
    // GIVEN
    URL noHandlerUrl = JWBF.newURLWithoutHandler("http://www.google.com");
    try {
      // WHEN
      noHandlerUrl.openConnection();
      fail();
    } catch (IOException e) {
      fail(e.getMessage());
    } catch (UnsupportedOperationException e) {
      // THEN
      assertEquals(null, e.getMessage());
    }
  }

  @Test
  public void testFormatVersionText() {

    String versionText = JWBF.formatVersionText();

    ImmutableList<String> expected = //
        ImmutableList.of(
            "jwbf-generic-core => DEVEL", //
            "jwbf-generic-mediawiki => DEVEL");
    GAssert.assertEquals(expected, splitVersionText(versionText));
  }

  public static ImmutableList<String> splitVersionText(String versionText) {
    return ImmutableList.copyOf(Splitter.on("\n").omitEmptyStrings().split(versionText));
  }
}
