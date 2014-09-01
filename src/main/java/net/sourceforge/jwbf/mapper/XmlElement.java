package net.sourceforge.jwbf.mapper;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class XmlElement {

  public static final XmlElement NULL_XML = new XmlElement(null);

  private final org.jdom2.Element element;

  XmlElement(org.jdom2.Element element) {
    this.element = element;
  }

  public String getQualifiedName() {
    return element.getQualifiedName();
  }

  @CheckForNull
  public String getAttributeValue(String name) {
    if (element == null) {
      return null;
    } else {
      return element.getAttributeValue(name);
    }
  }

  public Optional<String> getAttributeValueOpt(String name) {
    return Optional.fromNullable(getAttributeValue(name));
  }

  public String getAttributeValueNonNull(String name) {
    return Preconditions.checkNotNull(getAttributeValue(name), //
        "no attribute found for key: " + name);
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

  Optional<XmlElement> getChildOpt(String name) {
    XmlElement child = getChild(name);
    if (child == NULL_XML) {
      return Optional.absent();
    } else {
      return Optional.of(child);
    }
  }

  public XmlElement getChild(String name) {
    if (element == null) {
      return NULL_XML;
    } else {
      org.jdom2.Element child = element.getChild(name);
      if (child == null) {
        return NULL_XML;
      } else {
        return new XmlElement(child);
      }
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
    return toElements(element.getChildren());
  }

  public List<XmlElement> getChildren(String name) {
    return toElements(element.getChildren(name));
  }

  public boolean hasAttribute(String name) {
    org.jdom2.Attribute attribute = element.getAttribute(name);
    return attribute != null;
  }

  public String getText() {
    return element.getText();
  }

  public Optional<XmlElement> getErrorElement() {
    return XmlConverter.getErrorElement(this);
  }
}
