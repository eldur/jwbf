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

  private FilesDelegate files = new FilesDelegate();

  public OutputStream newOutputStream(Path path) {
    try {
      return files.newOutputStream(path);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public FileTime getLastModifiedTime(Path source) {
    try {
      return files.getLastModifiedTime(source);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public void writeBytes(Path filePath, byte[] bytes) {
    try {
      files.write(filePath, bytes);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public Path createTempDir(Path targetDir, String prefix) {
    try {
      return files.createTempDirectory(targetDir, prefix);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public Path createDirectory(Path parent, String dirName) {
    return createDirectory(parent.resolve(dirName));
  }

  public List<Path> listFiles(Path directory) {
    List<Path> fileNames = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = files.newDirectoryStream(directory)) {
      for (Path path : directoryStream) {
        fileNames.add(path);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return fileNames;
  }

  public List<Path> listFiles(Path directory, DirectoryStream.Filter filter) {
    List<Path> fileNames = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = files.newDirectoryStream(directory, filter)) {
      for (Path path : directoryStream) {
        fileNames.add(path);
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return fileNames;
  }

  public Path createDirectories(Path foo) {
    try {
      return files.createDirectories(foo);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public Path createDirectory(Path foo) {
    try {
      return files.createDirectory(foo);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  static class FilesDelegate {

    Path createDirectories(Path foo) throws IOException {
      return Files.createDirectories(foo);
    }

    Path createDirectory(Path foo) throws IOException {
      return Files.createDirectory(foo);
    }

    DirectoryStream newDirectoryStream(Path directory) throws IOException {
      return Files.newDirectoryStream(directory);
    }

    DirectoryStream newDirectoryStream(Path directory, DirectoryStream.Filter filter)
        throws IOException {
      return Files.newDirectoryStream(directory, filter);
    }

    Path createTempDirectory(Path dir, String prefix) throws IOException {
      return Files.createTempDirectory(dir, prefix);
    }

    void write(Path path, byte[] bytes) throws IOException {
      Files.write(path, bytes);
    }

    OutputStream newOutputStream(Path path) throws IOException {
      return Files.newOutputStream(path);
    }

    FileTime getLastModifiedTime(Path path) throws IOException {
      return Files.getLastModifiedTime(path);
    }
  }
}
