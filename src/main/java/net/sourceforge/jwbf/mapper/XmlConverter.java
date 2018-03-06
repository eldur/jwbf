package net.sourceforge.jwbf.mapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.internal.NonnullFunction;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import net.sourceforge.jwbf.mediawiki.actions.util.ApiException;

public final class XmlConverter {

  private XmlConverter() {
    // do nothing
  }

  private static final Logger log = LoggerFactory.getLogger(XmlConverter.class);

  public static Function<XmlElement, ApiException> toApiException() {
    return new NonnullFunction<XmlElement, ApiException>() {
      @Nonnull
      @Override
      public ApiException applyNonnull(@Nonnull XmlElement xmlElement) {
        String qualifiedName = xmlElement.getQualifiedName();
        if (qualifiedName != null && qualifiedName.equals("error")) {
          String code = xmlElement.getAttributeValue("code");
          String info = xmlElement.getAttributeValue("info");
          return new ApiException(code, info);
        } else {
          throw new IllegalArgumentException("only errors can be mapped to an exception");
        }
      }
    };
  }

  @Nonnull
  public static XmlElement getRootElementWithError(final String xml) {
    return Optionals.getOrThrow(getRootElementWithErrorOpt(xml), "Invalid XML: " + xml);
  }

  static Optional<XmlElement> getRootElementWithErrorOpt(String xml) {
    String xmlTrimmedInCauseOfMediawikiProblem = xml.trim();
    Optional<String> xmlStringOpt = Optionals.absentIfEmpty(xmlTrimmedInCauseOfMediawikiProblem);
    if (xmlStringOpt.isPresent()) {
      SAXBuilder builder = new SAXBuilder();
      org.jdom2.Element root;
      try {
        byte[] bytes = xmlTrimmedInCauseOfMediawikiProblem.getBytes(Charsets.UTF_8);
        Document doc = builder.build(new ByteArrayInputStream(bytes));

        root = doc.getRootElement();

      } catch (JDOMException e) {
        log.error(xml);
        return Optional.absent();
      } catch (IOException e) {
        throw new IllegalArgumentException(e);
      }
      if (root == null) {
        throw new ActionException("no root element found");
      }
      return Optional.of(new XmlElement(root));
    } else {
      return Optional.absent();
    }
  }

  /**
   * Determines if the given XML Document contains an error message which then would printed by the
   * logger.
   *
   * @param rootXmlElement XML <code>Document</code>
   * @return error element
   */
  static Optional<XmlElement> getErrorElement(XmlElement rootXmlElement) {
    Optional<XmlElement> elem = rootXmlElement.getChildOpt("error");
    if (elem.isPresent()) {
      ApiException error = elem.transform(toApiException()).get();
      log.error(error.getCode() + ": " + error.getValue());
    }
    return elem;
  }

  @Nonnull
  public static XmlElement getRootElement(String xml) {
    Optional<XmlElement> rootXmlElement = getRootElementWithErrorOpt(xml);
    if (!rootXmlElement.isPresent()) {
      throw new IllegalArgumentException("\"" + xml + "\" is no valid xml");
    }
    Optional<ApiException> apiException =
        getErrorElement(rootXmlElement.get()) //
            .transform(toApiException());
    if (apiException.isPresent()) {
      throw apiException.get();
    }
    return rootXmlElement.get();
  }

  public static String evaluateXpath(String xml, String xpath) {
    XPath parser = XPathFactory.newInstance().newXPath();
    try {
      XPathExpression titleParser = parser.compile(xpath);
      ByteArrayInputStream byteStream //
          = new ByteArrayInputStream(xml.getBytes(MediaWiki.getCharset()));
      InputSource in = new InputSource(byteStream);
      return titleParser.evaluate(in);
    } catch (XPathExpressionException | UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static Optional<XmlElement> getChildOpt(String xml, String first, String... childNames) {
    XmlElement child = getChild(xml, first, childNames);
    if (XmlElement.NULL_XML == child) {
      return Optional.absent();
    }
    return Optional.fromNullable(child);
  }

  @CheckForNull
  static XmlElement getChild(String xml, String first, String... childNames) {
    if (first == null) {
      return null;
    }
    ImmutableList<String> names =
        ImmutableList.<String>builder() //
            .add(first) //
            .addAll(ImmutableList.copyOf(childNames)) //
            .build();

    XmlElement rootElement = getRootElement(xml);
    return getChild(rootElement, names);
  }

  @CheckForNull
  private static XmlElement getChild(XmlElement element, ImmutableList<String> names) {
    if (element == null) {
      return null;
    } else if (names.isEmpty()) {
      return element;
    } else {
      ImmutableList<String> withoutFirst = names.subList(1, names.size());
      XmlElement child = element.getChild(Iterables.getFirst(names, null));
      return getChild(child, withoutFirst);
    }
  }

  public static void failOnError(String xml) {
    getChecked(xml).getClass();
  }

  public static XmlElement getChecked(String xml) {
    XmlElement root = getRootElementWithError(xml);
    Optional<ApiException> error = root.getErrorElement().transform(toApiException());
    if (error.isPresent()) {
      throw error.get();
    }
    return root;
  }
}
