package net.sourceforge.jwbf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class NioUnchecked {

  public static OutputStream newOutputStream(Path path) {
    try {
      return Files.newOutputStream(path);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static FileTime getLastModifiedTime(Path source) {
    try {
      return Files.getLastModifiedTime(source);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static void writeBytes(Path filePath, byte[] bytes) {
    try {
      Files.write(filePath, bytes);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static Path createTempDir(Path targetDir, String prefix) {
    try {
      return Files.createTempDirectory(targetDir, prefix);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static Path createDirectory(Path parent, String dirName) {
    return createDirectory(parent.resolve(dirName));
  }

  public static List<Path> listFiles(Path directory) {
    List<Path> fileNames = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
      for (Path path : directoryStream) {
        fileNames.add(path);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return fileNames;
  }

  public static List<Path> listFiles(Path directory, DirectoryStream.Filter filter) {
    List<Path> fileNames = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, filter)) {
      for (Path path : directoryStream) {
        fileNames.add(path);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return fileNames;
  }

  public static Path createDirectories(Path foo) {
    try {
      return Files.createDirectories(foo);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static Path createDirectory(Path foo) {
    try {
      return Files.createDirectory(foo);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
