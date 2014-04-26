package net.sourceforge.jwbf.extractXml;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Element {

  private final org.jdom.Element element;

  Element(org.jdom.Element element) {
    this.element = element;
  }

  public String getQualifiedName() {
    return element.getQualifiedName();
  }

  public String getAttributeValue(String name) {
    return element.getAttributeValue(name);
  }

  public Element getChild(String name) {
    org.jdom.Element child = element.getChild(name);
    if (child == null) {
      return null;
    } else {
      return new Element(child);
    }
  }

  private ImmutableList<Element> toElements(List<org.jdom.Element> list) {
    return ImmutableList.copyOf(Iterables.transform(list, TO_ELEMENT));
  }

  private static final Function<org.jdom.Element, Element> TO_ELEMENT = new Function<org.jdom.Element, Element>() {

    @Override
    public Element apply(@Nullable org.jdom.Element input) {
      if (input == null) {
        return null;
      } else {
        return new Element(input);
      }
    }
  };

  public List<Element> getChildren() {
    return toElements(element.getChildren());
  }

  public List<Element> getChildren(String name) {
    return toElements(element.getChildren(name));
  }

  public boolean hasAttribute(String name) {
    org.jdom.Attribute attribute = element.getAttribute(name);
    if (attribute == null) {
      return false;
    } else {
      return true;
    }
  }

  public String getText() {
    return element.getText();
  }

}
