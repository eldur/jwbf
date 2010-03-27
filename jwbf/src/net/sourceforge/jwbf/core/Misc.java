package net.sourceforge.jwbf.core;

import java.util.HashSet;
import java.util.Set;

public final class Misc {

    private Misc() {

    }
    /**
     * @param a a
     * @param b a
     * @return true if one or both sets are <code>null</code> or the intersection of sets is empty.
     */
    @SuppressWarnings("unchecked")
    public static boolean isIntersectionEmpty(Set<?> a, Set<?> b) {
        if (a != null && b != null) {
            Set<?> aTemp = new HashSet(a);
            Set<?> bTemp = new HashSet(b);
            aTemp.retainAll(bTemp);
            bTemp.retainAll(aTemp);
            return !(aTemp.size() > 0 && bTemp.size() > 0);
        }
        return true;
    }
}
