package net.sourceforge.jwbf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import lombok.val;
import net.sourceforge.jwbf.JWBF.ContainerEntry;
import net.sourceforge.jwbf.core.actions.HttpActionClient;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class JWBFTest {

  @Before
  public void before() {
    JWBF.cache = null;
  }

  @Test
  public void testInit_invalidFile() {
    // GIVEN
    val url = JWBF.newURL("file:///a");
    val packageName = "";

    // WHEN
    val result = JWBF.init(packageName, url);

    // THEN
    val expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_bundle() {
    // GIVEN
    val url = JWBF.newURLWithoutHandler("bundle:///a");
    val packageName = "";

    // WHEN
    val result = JWBF.init(packageName, url);

    // THEN
    val expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_whatEver() {
    // GIVEN
    val url = JWBF.newURLWithoutHandler("whatEver:///a");
    val packageName = "";

    // WHEN
    val result = JWBF.init(packageName, url);

    // THEN
    val expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_jar() {
    // GIVEN
    val url = JWBF.newURL("jar:file:/home/noOne/lib/jwbf.jar!/net/sourceforge/jwbf");
    val packageName = "net/sourceforge/jwbf";

    // WHEN
    val result = JWBF.init(packageName, url);

    // THEN
    val expected = ImmutableMap.of();
    assertEquals(expected, result);
  }

  @Test
  public void testInit_realFile() throws Exception {
    // GIVEN
    val targetDir = getTargetDir();
    val tempDirectory = Files.createTempDirectory(targetDir.toPath(), "manifest-test");
    val manifestFile = new File(tempDirectory.toFile(), "MANIFEST.MF");
    String expectedVersion = "999.0.0-SNAPSHOT-${buildNumber}";
    String expectedTitle = "jwbf";
    byte[] bytes = Joiner.on("\n").join("Manifest-Version: 1.0", //
        "Implementation-Title: " + expectedTitle, //
        "Implementation-Version: " + expectedVersion, "") //
        .getBytes();
    Files.write(manifestFile.toPath(), bytes);

    String name = MediaWikiBot.class.getPackage().getName();
    ImmutableList<String> fileNames = ImmutableList.copyOf(Splitter.on(".").split(name));

    File first = new File(tempDirectory.toFile(), Iterables.getFirst(fileNames, null));
    for (String fileName : Iterables.skip(fileNames, 1)) {
      first = new File(first, fileName);
    }
    first.mkdirs();
    val packageName = name.replace(".", "/");
    val url = tempDirectory.toUri().toURL();
    // WHEN
    val result = JWBF.init(packageName, url);

    // THEN
    val expected = ImmutableMap.<String, String> builder() //
        .put(expectedTitle + "-mediawiki", expectedVersion) //
        .build();
    assertEquals(expected, result);

  }

  private File getTargetDir() {
    val targetDir = new File("target");
    if (!(targetDir.exists() && targetDir.isDirectory())) {
      fail("no target dir found");
    }
    return targetDir;
  }

  @Test
  public void testGetVersions() {
    // GIVEN
    JWBF.cache = ImmutableMap.<String, String> builder() //
        .put("jwbf-generic-mediawiki", JWBF.DEVEL) //
        .build();

    // WHEN
    String version = JWBF.getVersion(MediaWikiBot.class);

    // THEN
    assertEquals(JWBF.DEVEL, version);
  }

  @Test
  public void testGetPartId() {
    // GIVEN
    JWBF.cache = ImmutableMap.<String, String> builder() //
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
    JWBF.cache = ImmutableMap.<String, String> builder() //
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
    List<ContainerEntry> elements = ImmutableList.of( //
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
        new ContainerEntry("net/sourceforge/jwbf/mediawiki/actions/queries/BacklinkTitles$1.class",
            false) //
        );
    String packageName = "net/sourceforge/jwbf";

    // WHEN
    val result = JWBF.makeVersionMap(packageName, null, elements);

    // THEN
    val expected = ImmutableMap.<String, String> builder() //
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
    val jwbfVersion = JWBF.getVersion(this.getClass());

    // THEN
    assertEquals("Version unknown", jwbfVersion);
  }

  @Test
  public void testJarToEntries() throws Exception {

    // GIVEN
    val targetDir = getTargetDir();
    val tempDirectory = Files.createTempDirectory(targetDir.toPath(), "jarfile-test");

    File jarFile = new File(tempDirectory.toFile(), "a.jar");
    File inputDir = new File(tempDirectory.toFile(), "jarContent");
    inputDir.mkdir();
    new File(inputDir, "test").createNewFile();
    newJarFile(jarFile, inputDir);

    String jarFileName = jarFile.getAbsolutePath();

    // WHEN
    val result = JWBF.jarToEntries(jarFileName);

    Function<ContainerEntry, ContainerEntry> function = new Function<ContainerEntry, ContainerEntry>() {

      @Override
      public ContainerEntry apply(ContainerEntry input) {
        if (input == null) {
          return null;
        }
        return new ContainerEntry(input.getName().replaceAll("[0-9]+", ""), input.isDirectory());
      }
    };
    // THEN
    val mutated = Ordering.usingToString().immutableSortedCopy(Lists.transform(result, function));

    val expected = ImmutableList.<ContainerEntry> builder() //
        .add(new ContainerEntry("META-INF/MANIFEST.MF", false)) //
        .add(new ContainerEntry("target/jarfile-test/jarContent/", true)) //
        .add(new ContainerEntry("target/jarfile-test/jarContent/test/", true)) //
        .build();
    assertEquals(expected, mutated);

  }

  public void newJarFile(File jarFile, File inputDir) throws IOException {
    Manifest manifest = new Manifest();
    manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFile), manifest);
    addRecursiv(inputDir, target);
    target.close();
  }

  private void addRecursiv(File source, JarOutputStream target) throws IOException {
    if (source.isDirectory()) {
      JarEntry entry = makeEntry(source);
      target.putNextEntry(entry);
      target.closeEntry();
      for (File nestedFile : source.listFiles()) {
        addRecursiv(nestedFile, target);
      }
      return;
    }

    addFile(source, target);
  }

  private void addFile(File source, JarOutputStream target) throws IOException,
      FileNotFoundException {
    JarEntry entry = makeEntry(source);
    target.putNextEntry(entry);

    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));) {

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
  }

  private JarEntry makeEntry(File source) {
    String name = source.getPath().replace("\\", "/");
    if (!name.isEmpty()) {
      if (!name.endsWith("/")) {
        name += "/";
      }
    }
    JarEntry entry = new JarEntry(name);
    entry.setTime(source.lastModified());
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
      assertEquals("\\invalid", e.getMessage());
    }
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
  public void testReadFromManifest() throws Exception {
    // GIVEN
    Manifest manifest = new Manifest();
    String fallback = "fallback";

    // WHEN
    String value = JWBF.readFromManifest(manifest, "test", fallback);

    // THEN
    assertEquals(fallback, value);
  }

}
