/*
 * Copyright 2007 Thomas Stock.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Contributors:
 *
 */
package net.sourceforge.jwbf;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import lombok.val;

/**
 * @author Thomas Stock
 */
public final class JWBF {

  private static boolean errorInfo = true;
  static Map<String, String> cache = null;

  static final String DEVEL = "DEVEL";
  static final char SEPARATOR_CHAR = '/';
  private static final String JAR_FILE_INDEX = "jar:file:";
  private static final String FILE_INDEX = "file:";

  private static final FileFilter DIRECTORY_FILTER = new FileFilter() {

    @Override
    public boolean accept(File f) {
      return f.isDirectory();
    }
  };

  private JWBF() {
    // do nothing
  }

  private static Map<String, String> lazyVersion() {
    val clazz = JWBF.class;
    val packageName = packageNameOf(clazz);
    val url = urlOf(clazz, packageName);
    synchronized (JWBF.class) {
      if (cache == null) {
        synchronized (JWBF.class) {
          cache = init(packageName, url);
        }
      }
    }
    return cache;
  }

  static Map<String, String> init(String packageName, URL url) {
    val lowerCaseUrl = urlExtract(url).toLowerCase();
    if (lowerCaseUrl.startsWith(JAR_FILE_INDEX)) {
      return jarManifestLookup(packageName, url);
    } else if (lowerCaseUrl.startsWith(FILE_INDEX)) {
      return fileSystemManifestLookup(packageName, url);
    } else {
      return new HashMap<>();
    }
  }

  private static URL urlOf(Class<?> clazz, String packagename) {
    return clazz.getClassLoader().getResource(packagename);
  }

  private static String packageNameOf(Class<?> clazz) {
    return clazz.getPackage().getName().replace('.', SEPARATOR_CHAR);
  }

  private static Map<String, String> fileSystemManifestLookup(String packageName, URL url) {
    Manifest manifest = findManifest(urlToFile(url).getAbsolutePath());
    File root = urlToFile(url);

    File[] dirs = root.listFiles(DIRECTORY_FILTER);
    List<ContainerEntry> entries = filesToEntries(dirs, packageName);
    return makeVersionMap(packageName, manifest, entries);
  }

  private static Map<String, String> jarManifestLookup(String packageName, URL url) {
    int jarEnd = urlExtract(url).indexOf("!" + SEPARATOR_CHAR);
    val jarFileName = urlExtract(url).substring(JAR_FILE_INDEX.length(), jarEnd);
    Manifest manifest = findManifest(jarFileName);
    List<ContainerEntry> elements = jarToEntries(jarFileName);
    return makeVersionMap(packageName, manifest, elements);
  }

  private static String urlExtract(URL url) {
    return url.toExternalForm();
  }

  static Map<String, String> makeVersionMap(String packageName, Manifest manifest,
      List<ContainerEntry> elements) {

    Map<String, String> parts = new HashMap<>();
    for (ContainerEntry element : elements) {

      String jarEntryName = element.getName();
      String shlashes = jarEntryName.replaceAll("[a-zA-Z0-9]", "");
      if (element.isDirectory() && jarEntryName.contains(packageName) && shlashes.length() == 4) {

        String wikiSystemName = jarEntryName.split(SEPARATOR_CHAR + "")[3];
        String artifactId = readMFProductTitle(manifest) + "-" + wikiSystemName;
        String readMFVersion = readMFVersion(manifest);

        parts.put(artifactId, readMFVersion);
      }
    }

    return parts;
  }

  private static List<ContainerEntry> filesToEntries(File[] dirs, String packageName) {
    List<ContainerEntry> result = new ArrayList<>();
    if (dirs != null) {
      for (File f : dirs) {
        if (f.isDirectory()) {
          String name = makeFileName(f, packageName);
          if (!name.isEmpty()) {
            result.add(new ContainerEntry(name, true));
          }
          result.addAll(filesToEntries(f.listFiles(DIRECTORY_FILTER), packageName));
        }
      }
    }
    return result;
  }

  private static String makeFileName(File file, String packageName) {
    String path = file.getAbsolutePath();
    int indexOfPackage = path.indexOf(packageName);
    if (indexOfPackage == -1) {
      return "";
    }
    return path.substring(indexOfPackage);
  }

  static List<ContainerEntry> jarToEntries(String jarFileName) {
    List<ContainerEntry> result = new ArrayList<>();

    try (JarFile jar = new JarFile(jarFileName)) {
      Enumeration<JarEntry> je = jar.entries();
      while (je.hasMoreElements()) {
        JarEntry jarEntry = je.nextElement();
        boolean directory = jarEntry.isDirectory();
        result.add(new ContainerEntry(jarEntry.getName(), directory));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * @param clazz
   *          of the module
   * @return the version
   */
  public static String getVersion(Class<?> clazz) {
    return getPartInfo(lazyVersion(), clazz, "Version unknown").version;
  }

  /**
   * @param clazz
   *          of the module
   * @return the version
   */
  public static String getPartId(Class<?> clazz) {
    return getPartInfo(lazyVersion(), clazz, "No Module for " + clazz.getName()).id;
  }

  private static PartContainer getPartInfo(Map<String, String> versionDetails, Class<?> clazz,
      String fallbackValue) {
    String[] packageParts = clazz.getPackage().getName().split("\\.");
    if (packageParts.length > 3) {
      String classContainer = packageParts[3];
      for (Entry<String, String> entry : versionDetails.entrySet()) {
        if (entry.getKey().contains(classContainer)) {
          return new PartContainer(entry.getKey(), entry.getValue());
        }
      }
    }
    return new PartContainer(fallbackValue, fallbackValue);
  }

  private static class PartContainer {
    final String id;
    final String version;

    public PartContainer(String id, String version) {
      this.id = id;
      this.version = version;
    }
  }

  /**
   * Prints the JWBF Version.
   */
  public static void printVersion() {
    System.out.println(getVersions());
  }

  public static void main(String[] args) {
    printVersion();
  }

  /**
   * @return the JWBF Version.
   */
  public static Map<String, String> getVersions() {
    return Collections.unmodifiableMap(lazyVersion());
  }

  private static String readMFVersion(Manifest manifest) {
    return readFromManifest(manifest, "Implementation-Version", DEVEL);
  }

  private static String readMFProductTitle(Manifest mainfest) {
    return readFromManifest(mainfest, "Implementation-Title", "jwbf-generic");
  }

  static String readFromManifest(Manifest manifest, String manifestKey, String fallback) {

    if (manifest == null) {
      return logAndReturn(fallback);
    }
    val result = manifest.getMainAttributes().getValue(manifestKey);
    if (result == null) {
      return logAndReturn(fallback);
    }
    return result;
  }

  private static String logAndReturn(String fallback) {
    if (errorInfo) {
      errorInfo = false;
      val msg = "E: no MANIFEST.MF found, please create it.";
      System.err.println(msg);
    }
    return fallback;
  }

  private static Manifest findManifest(String filesystemPath) {
    URL manifestUrl;
    if (filesystemPath.endsWith(".jar")) {

      manifestUrl = newURL(JAR_FILE_INDEX + filesystemPath + "!/META-INF/MANIFEST.MF");
    } else {
      if (!filesystemPath.endsWith(File.separator)) {
        filesystemPath += File.separatorChar;
      }
      manifestUrl = searchMF(filesystemPath);
    }
    if (manifestUrl != null) {
      return newManifest(manifestUrl);
    }
    return null;
  }

  private static Manifest newManifest(URL manifestUrl) {
    try {
      return new Manifest(manifestUrl.openStream());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static URL searchMF(String fileName) {
    if (fileName == null) {
      return null;
    }
    val file = new File(fileName);
    val manifestFileName = "MANIFEST.MF";
    val manifestFile = new File(file, manifestFileName);
    if (manifestFile.exists()) {
      return newURL(FILE_INDEX + file + File.separatorChar + manifestFileName);
    } else {
      return searchMF(file.getParent());
    }
  }

  static class ContainerEntry {

    private final String name;
    private final boolean directory;

    public ContainerEntry(String name, boolean directory) {
      this.name = name;
      this.directory = directory;
    }

    public String getName() {
      return name;
    }

    public boolean isDirectory() {
      return directory;
    }

    @Override
    public String toString() {
      return Objects.toString(name) + " " + directory;
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, directory);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      } else if (obj == this) {
        return true;
      } else if (obj instanceof ContainerEntry) {
        val that = (ContainerEntry) obj;
        return Objects.equals(this.name, that.name) && //
            Objects.equals(this.directory, that.directory) //
        ;
      } else {
        return false;
      }
    }

  }

  public static URL newURLWithoutHandler(final String url) {
    try {
      return new URL(null, url, new UnsupportedHandler());
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(url, e);
    }
  }

  public static URL newURL(final String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(url, e);
    }
  }

  private static class UnsupportedHandler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      throw new UnsupportedOperationException();
    }

  }

  public static File urlToFile(URL url) {
    String urlString = url.toString();
    return new File(toUri(urlString));
  }

  public static URI toUri(String urlString) {
    try {
      return new URI(urlString);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(urlString, e);
    }
  }
}
