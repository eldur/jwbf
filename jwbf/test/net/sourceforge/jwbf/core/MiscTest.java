package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.jwbf.JWBF;

import org.junit.Test;


public class MiscTest {
  @Test
  public void subsetTest() {

    assertFalse(!Misc.isIntersectionEmpty(null, null));
    Set<String> a = new HashSet<String>();
    Set<String> b = new HashSet<String>();
    assertTrue(a.containsAll(b));
    assertTrue(Misc.isIntersectionEmpty(a, b));
    assertTrue(Misc.isIntersectionEmpty(b, a));

    b.add("a");
    b.add("c");
    assertFalse(!Misc.isIntersectionEmpty(a, b));
    assertFalse(!Misc.isIntersectionEmpty(b, a));
    a.add("a");
    a.add("b");

    assertTrue(!Misc.isIntersectionEmpty(a, b));
    assertTrue(!Misc.isIntersectionEmpty(b, a));
    assertTrue(a.size() > 1);
    assertTrue(b.size() > 1);
  }

  @Test
  public void testGetVersion() {
    String jwbfVersion = JWBF.getVersion(this.getClass());
    System.out.println(jwbfVersion);
    assertTrue(jwbfVersion.length() > 2);
  }

  @Test
  public void testInit() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Class<JWBF> clazz = JWBF.class;
    Method [] methods = clazz.getDeclaredMethods();
    Method initMethod = null;
    for (Method method : methods) {
      if (method.getName().equals("init")) {
        initMethod = method;
        break;
      }
    }
    assertNotNull("no init method found", initMethod);
    if (initMethod == null) return; // XXX for eclipse
    if (!initMethod.isAccessible()) {
      initMethod.setAccessible(true);
    }

    File manifestFile = null;
    File newManifestFile = null;
    File targetDir = new File("target");
    if (targetDir.exists() && targetDir.isDirectory()) {
      manifestFile = new File(targetDir, "MANIFEST.MF");
      if (manifestFile.exists()) {
        newManifestFile = new File(targetDir, "FM.TSEFINAM");
        manifestFile.renameTo(newManifestFile);
        assertTrue(newManifestFile.exists() && !manifestFile.exists());
      }
    } else {
      fail("no target dir found");
    }

    initMethod.invoke(null, clazz);
    if (manifestFile != null && newManifestFile != null) {
      newManifestFile.renameTo(manifestFile);
      assertTrue(!newManifestFile.exists() && manifestFile.exists());
    }
    for (Field f : clazz.getDeclaredFields()) {
      if (!f.isAccessible()) f.setAccessible(true);
      String name = f.getName();
      if ("title".equals(name)) {
        assertEquals("jwbf-generic", f.get(clazz));
      } else if ("version".equals(name)) {
        assertEquals("DEVEL", f.get(clazz));
      }
    }


  }

}
