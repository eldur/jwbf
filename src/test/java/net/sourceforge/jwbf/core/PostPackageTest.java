package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import org.junit.Test;

public class PostPackageTest {

  @Test
  public void testJarManifest() throws Exception {
    File mvnTargetDir = new File("target");
    assumeTrue(mvnTargetDir.exists());
    File[] files = mvnTargetDir.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return name.contains(".jar") && !name.contains("javadoc") && !name.contains("sources");
      }
    });

    for (File jar : files) {

      BufferedReader in = null;
      try {
        String text = "";
        StringWriter strOut = new StringWriter();
        Process p = Runtime.getRuntime().exec("java -jar " + jar.getAbsolutePath() + " JWBF.");
        in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((text = in.readLine()) != null) {
          strOut.write(text);
          strOut.flush();
        }
        String out = strOut.toString();
        if (!Strings.isNullOrEmpty(out)) {
          assertTrue("should contain jwbf, but was: \"" + out + "\" <= " + jar.getAbsolutePath(),
              out.contains("jwbf"));
        }

      } finally {
        if (in != null)
          in.close();
      }
    }
  }

  @Test
  public void testImportsForSimpleStart() throws Exception {
    File file = new File("src/main/java/net/sourceforge/jwbf/JWBF.java");
    List<String> content = Resources.readLines(file.toURI().toURL(), Charsets.UTF_8);
    assertTrue(content.size() > 1);
    Collection<String> imports = Collections2.filter(content, new Predicate<String>() {

      @Override
      public boolean apply(String line) {
        return line.startsWith("import ");
      }
    });
    Collection<String> invalidImports = Collections2.filter(imports, new Predicate<String>() {

      Set<String> validImports = ImmutableSet.of( //
          "^import java\\.io.*" //
          , "^import java\\.net.*" //
          , "^import java\\.util.*" //
          , "^import lombok\\.val;" //
      );

      @Override
      public boolean apply(String line) {
        boolean result = true;
        for (String validImportRegex : validImports) {
          result = line.matches(validImportRegex);
          if (result) {
            break;
          }
        }
        return !result;
      }
    });
    assertTrue("Do not use this import: " + invalidImports.toString(), invalidImports.isEmpty());
  }
}
