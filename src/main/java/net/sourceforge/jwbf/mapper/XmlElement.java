package net.sourceforge.jwbf.mapper;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.sourceforge.jwbf.core.internal.Checked;

public class XmlElement {

  public static final XmlElement NULL_XML = new XmlElement(null);

  private final Optional<org.jdom2.Element> element;

  XmlElement(org.jdom2.Element element) {
    this.element = Optional.fromNullable(element);
  }

  @CheckForNull
  public String getQualifiedName() {
    if (element.isPresent()) {
      return element.get().getQualifiedName();
    } else {
      return null;
    }
  }

  @CheckForNull
  public String getAttributeValue(String name) {
    if (element.isPresent()) {
      return element.get().getAttributeValue(name);
    } else {
      return null;
    }
  }

  public Optional<String> getAttributeValueOpt(String name) {
    return Optional.fromNullable(getAttributeValue(name));
  }

  public String getAttributeValueNonNull(String name) {
    return Checked.nonNull(getAttributeValue(name), "attribute value for key: " + name);
  }

  @Deprecated
  @CheckForNull
  public String getChildAttributeValue(String childName, String attributeName) {
    XmlElement child = getChild(childName);
    if (child == NULL_XML) {
      return null;
    }
    return child.getAttributeValue(attributeName);
  }

  public Optional<XmlElement> getChildOpt(String name) {
    XmlElement child = getChild(name);
    if (child == NULL_XML) {
      return Optional.absent();
    } else {
      return Optional.of(child);
    }
  }

  public XmlElement getChild(String name) {
    if (element.isPresent()) {
      org.jdom2.Element child = element.get().getChild(name);
      if (child == null) {
        return NULL_XML;
      } else {
        return new XmlElement(child);
      }
    } else {
      return NULL_XML;
    }
  }

  private ImmutableList<XmlElement> toElements(List<org.jdom2.Element> list) {
    return ImmutableList.copyOf(Iterables.transform(list, TO_ELEMENT));
  }

  private static final Function<org.jdom2.Element, XmlElement> TO_ELEMENT =
      new Function<org.jdom2.Element, XmlElement>() {

        @Override
        public XmlElement apply(@Nullable org.jdom2.Element input) {
          if (input == null) {
            return null;
          } else {
            return new XmlElement(input);
          }
        }
      };

  public List<XmlElement> getChildren() {
    if (element.isPresent()) {
      return toElements(element.get().getChildren());
    } else {
      return ImmutableList.of();
    }
  }

  public List<XmlElement> getChildren(String name) {
    if (element.isPresent()) {
      return toElements(element.get().getChildren(name));
    } else {
      return ImmutableList.of();
    }
  }

  public boolean hasAttribute(String name) {
    if (element.isPresent()) {
      org.jdom2.Attribute attribute = element.get().getAttribute(name);
      return attribute != null;
    } else {
      return false;
    }
  }

  @CheckForNull
  public String getText() {
    if (element.isPresent()) {
      return element.get().getText();
    } else {
      return null;
    }
  }

  public Optional<XmlElement> getErrorElement() {
    return XmlConverter.getErrorElement(this);
  }
}
