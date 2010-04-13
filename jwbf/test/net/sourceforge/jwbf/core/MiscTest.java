package net.sourceforge.jwbf.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        System.out.println(JWBF.getVersion(this.getClass()));
    }

}
