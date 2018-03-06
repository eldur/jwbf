package net.sourceforge.jwbf;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NioUncheckedTest {

  @Mock private NioUnchecked.FilesDelegate files;

  @InjectMocks private NioUnchecked testee;

  @Test(expected = IllegalStateException.class)
  public void testcreateDirectory() throws IOException {
    Path path = mock(Path.class);
    when(files.createDirectory(path)).thenThrow(IOException.class);
    testee.createDirectory(path);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testCreateDirectories() throws IOException {
    Path path = mock(Path.class);
    when(files.createDirectories(path)).thenThrow(IOException.class);
    testee.createDirectories(path);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testListFilesWithFilter() throws IOException {
    Path path = mock(Path.class);
    DirectoryStream.Filter filter = mock(DirectoryStream.Filter.class);
    when(files.newDirectoryStream(path, filter)).thenThrow(IOException.class);
    testee.listFiles(path, filter);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testListFiles() throws IOException {
    Path path = mock(Path.class);
    when(files.newDirectoryStream(path)).thenThrow(IOException.class);
    testee.listFiles(path);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testCreateTempDirectory() throws IOException {
    Path path = mock(Path.class);
    String prefix = "test";
    when(files.createTempDirectory(path, prefix)).thenThrow(IOException.class);
    testee.createTempDir(path, prefix);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testWrite() throws IOException {
    Path path = mock(Path.class);
    byte[] bytes = new byte[0];
    Mockito.doThrow(IOException.class).when(files).write(path, bytes);
    testee.writeBytes(path, bytes);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testNewOutputStream() throws IOException {
    Path path = mock(Path.class);
    when(files.newOutputStream(path)).thenThrow(IOException.class);
    testee.newOutputStream(path);
    fail();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetLastModifiedTime() throws IOException {
    Path path = mock(Path.class);
    when(files.getLastModifiedTime(path)).thenThrow(IOException.class);
    testee.getLastModifiedTime(path);
    fail();
  }
}
