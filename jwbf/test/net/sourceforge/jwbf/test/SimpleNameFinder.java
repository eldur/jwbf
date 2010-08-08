package net.sourceforge.jwbf.test;

import java.util.List;


public class SimpleNameFinder implements NameFinder {

  public String getName(List<?> list, int i) {
    Object [] o = (Object []) list.get(i);
    return o[0].toString();
  }

}
