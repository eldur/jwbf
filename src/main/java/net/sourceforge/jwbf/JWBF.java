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
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** @author Thomas Stock */
public final class JWBF {

  // visible only for internal testing
  public static final String VERSION_FALLBACK_VALUE = "Version unknown";

  private static boolean errorInfo = true;
  static Map<String, String> cache = null;

  private static NioUnchecked nio = new NioUnchecked();

  static final String DEVEL_NAME = "jwbf-generic";
  static final String DEVEL_VERSION = "DEVEL";

  static final char SEPARATOR_CHAR = '/';
  private static final String JAR_FILE_INDEX = "jar:file:";
  private static final String FILE_INDEX = "file:";

  private static final DirectoryStream.Filter<Path> DIRECTORY_FILTER = //
      new DirectoryStream.Filter<Path>() {

        @Override
        public boolean accept(Path entry) throws IOException {
          return Files.isDirectory(entry);
        }
      };

  private JWBF() {
    // do nothing
  }

  private static Map<String, String> lazyVersion() {
    Class<?> clazz = JWBF.class;
    String packageName = packageNameOf(clazz);
    URL url = urlOf(clazz, packageName);
    if (cache == null) {
      synchronized (JWBF.class) {
        if (cache == null) {
          cache = init(packageName, toUri(url));
        }
      }
    }
    return cache;
  }

  static Map<String, String> init(String packageName, URI uri) {
    String lowerCaseUrl = urlExtract(uri).toLowerCase();

    if (lowerCaseUrl.startsWith(JAR_FILE_INDEX)) {
      return jarVersionDetails(packageName, uri);
    } else if (lowerCaseUrl.startsWith(FILE_INDEX) || exists(uri)) {
      return fileSystemVersionDetails(packageName, uri);
    } else {
      System.err.println("W: unknown uri: " + uri);
      return new HashMap<>();
    }
  }

  static URI stripJarSuffix(URI uri) {
    try {
      URLConnection urlConnection = toUrl(uri).openConnection();
      if (urlConnection instanceof JarURLConnection) {
        JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
        return toUri(jarURLConnection.getJarFileURL());
      } else {
        return uri;
      }
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  static boolean exists(URI uri) {
    try {
      return Files.exists(Paths.get(uri));
    } catch (FileSystemNotFoundException e) {
      return false;
    }
  }

  private static URL urlOf(Class<?> clazz, String packageName) {
    return clazz.getClassLoader().getResource(packageName);
  }

  private static String packageNameOf(Class<?> clazz) {
    return clazz.getPackage().getName().replace('.', SEPARATOR_CHAR);
  }

  private static Map<String, String> fileSystemVersionDetails(String packageName, URI url) {
    Path root = uriToPath(url);
    if (Files.exists(root)) {
      List<Path> dirs = nio.listFiles(root, DIRECTORY_FILTER);
      List<ContainerEntry> entries = filesToEntries(dirs, packageName);
      return makeVersionMap(packageName, entries, DEVEL_VERSION, DEVEL_NAME);
    } else {
      return new HashMap<>();
    }
  }

  static Map<String, String> jarVersionDetails(String packageName, URI uri) {
    List<ContainerEntry> elements = jarToEntries(uri);
    String implementationTitle = getImplementationTitle();
    String implementationVersion = getImplementationVersion();
    return makeVersionMap(packageName, elements, implementationVersion, implementationTitle);
  }

  private static String urlExtract(URI uri) {
    return uri.toString();
  }

  static Map<String, String> makeVersionMap(
      String packageName,
      List<ContainerEntry> elements,
      String implementationVersion,
      String implementationTitle) {

    Map<String, String> parts = new HashMap<>();
    for (ContainerEntry element : elements) {

      String elementName = element.getName();

      boolean isDirectory = element.isDirectory();

      int slashCount = countSlashes(removeTrailingSlash(elementName));

      if (isDirectory && slashCount == 4 && elementName.contains(packageName)) {
        String wikiSystemName = elementName.split(SEPARATOR_CHAR + "")[3];
        String artifactId = implementationTitle + "-" + wikiSystemName;
        parts.put(artifactId, implementationVersion);
      }
    }

    return parts;
  }

  static String removeTrailingSlash(String str) {
    return str.replaceFirst("([^/])/$", "$1");
  }

  static int countSlashes(String str) {
    String s = str.replaceAll("[/]+", "");
    return str.length() - s.length();
  }

  private static List<ContainerEntry> filesToEntries(Iterable<Path> dirs, String packageName) {
    List<ContainerEntry> result = new ArrayList<>();
    if (dirs != null) {
      for (Path f : dirs) {
        if (Files.isDirectory(f)) {
          String name = makeFileName(f, packageName);
          if (!name.isEmpty()) {
            result.add(new ContainerEntry(name, true));
          }
          result.addAll(filesToEntries(nio.listFiles(f, DIRECTORY_FILTER), packageName));
        }
      }
    }
    return result;
  }

  private static String makeFileName(Path file, String packageName) {
    String path = file.toAbsolutePath().toUri().toString();
    int indexOfPackage = path.indexOf(packageName);
    if (indexOfPackage == -1) {
      return "";
    }
    return path.substring(indexOfPackage);
  }

  static List<ContainerEntry> jarToEntries(URI jarFileName) {
    List<ContainerEntry> result = new ArrayList<>();
    URI jarSimpleName = stripJarSuffix(jarFileName);

    if (jarSimpleName != null) {
      try {
        try (JarFile jar = new JarFile(new File(jarSimpleName))) {
          Enumeration<JarEntry> je = jar.entries();
          while (je.hasMoreElements()) {
            JarEntry jarEntry = je.nextElement();
            boolean directory = jarEntry.isDirectory();
            result.add(new ContainerEntry(jarEntry.getName(), directory));
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (RuntimeException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * @param clazz of the module
   * @return the version
   */
  public static String getVersion(Class<?> clazz) {
    return getPartInfo(lazyVersion(), clazz, VERSION_FALLBACK_VALUE).version;
  }

  /**
   * @param clazz of the module
   * @return the version
   */
  public static String getPartId(Class<?> clazz) {
    return getPartInfo(lazyVersion(), clazz, "No Module for " + clazz.getName()).id;
  }

  private static PartContainer getPartInfo(
      Map<String, String> versionDetails, Class<?> clazz, String fallbackValue) {
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

  /** Prints the JWBF Version. */
  public static void printVersion() {
    System.out.println("JWBF.");
    System.out.println(formatVersionText());
  }

  static String formatVersionText() {

    List<String> lines = new ArrayList<>();
    for (Entry<String, String> entry : getVersions().entrySet()) {
      lines.add(entry.getKey() + " => " + entry.getValue());
    }

    Collections.sort(lines);
    StringBuilder sb = new StringBuilder();
    for (String line : lines) {
      sb.append(line).append("\n");
    }

    return sb.toString();
  }

  public static void main(String[] args) {
    printVersion();
  }

  /** @return the JWBF Version. */
  public static Map<String, String> getVersions() {
    return Collections.unmodifiableMap(lazyVersion());
  }

  private static String getImplementationVersion() {
    return secondIfNull(objPackage().getImplementationVersion(), DEVEL_VERSION);
  }

  private static String getImplementationTitle() {
    return secondIfNull(objPackage().getImplementationTitle(), DEVEL_NAME);
  }

  private static Package objPackage() {
    return JWBF.class.getPackage();
  }

  static String secondIfNull(String str, String orStr) {
    if (str == null) {
      return logAndReturn(orStr);
    } else {
      return str;
    }
  }

  private static String logAndReturn(String fallback) {
    if (errorInfo) {
      errorInfo = false;
      System.err.println("E: no MANIFEST.MF found, please create it.");
    }
    return fallback;
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
        ContainerEntry that = (ContainerEntry) obj;
        return Objects.equals(this.name, that.name)
            && //
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
      throw new IllegalArgumentException("\"" + url + "\"", e);
    }
  }

  private static class UnsupportedHandler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
      throw new UnsupportedOperationException();
    }
  }

  static Path uriToPath(URI uri) {
    return Paths.get(uri);
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

  static URI toUri(URL url) {
    // url.toUri() -> new URI(toString());
    return toUri(url.toString());
  }

  static URL toUrl(URI uri) {
    try {
      return uri.toURL();
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(uri.toString(), e);
    }
  }
}
