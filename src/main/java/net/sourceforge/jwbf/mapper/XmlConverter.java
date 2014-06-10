package net.sourceforge.jwbf.mapper;

import javax.annotation.Nullable;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import net.sourceforge.jwbf.core.Optionals;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.MediaWiki;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class XmlConverter {

  private static final Logger log = LoggerFactory.getLogger(XmlConverter.class);
  private static final Function<XmlElement, XmlElement> GET_ERROR = new Function<XmlElement, XmlElement>() {
    @Nullable
    @Override
    public XmlElement apply(@Nullable XmlElement input) {
      XmlElement errorElement = getErrorElement(input);
      if (errorElement == null) {
        return XmlElement.NULL_XML;
      }
      return errorElement;
    }
  };

  public static Optional<XmlElement> getRootElementWithError(String xml) {
    Optional<String> xmlStringOpt = Optionals.absentIfEmpty(xml);
    if (xmlStringOpt.isPresent()) {
      SAXBuilder builder = new SAXBuilder();
      org.jdom.Element root = null;
      try {
        Document doc = builder.build(new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)));

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

  static XmlElement getErrorElement(XmlElement rootXmlElement) {
    XmlElement elem = rootXmlElement.getChild("error");
    if (elem != null) {
      log.error(elem.getAttributeValue("code") + ": " + elem.getAttributeValue("info"));
    }
    return elem;
  }

  public static XmlElement getRootElement(String xml) {
    Optional<XmlElement> rootXmlElement = getRootElementWithError(xml);
    if (!rootXmlElement.isPresent()) {
      throw new IllegalArgumentException(xml + " is no valid xml");
    }
    Optional<XmlElement> errorElement = absentIfNullXml(rootXmlElement.transform(GET_ERROR));
    if (errorElement.isPresent()) {
      String xmlError = xml;
      if (xmlError.length() > 700) {
        xmlError = xmlError.substring(0, 700);
      }
      throw new ProcessException(xmlError);
    }
    return rootXmlElement.get();
  }

  private static Optional<XmlElement> absentIfNullXml(Optional<XmlElement> elem) {
    if (elem.isPresent() && elem.get().equals(XmlElement.NULL_XML)) {
      elem = Optional.absent();
    }
    return elem;
  }

  public static String evaluateXpath(String s, String xpath) {
    XPath parser = XPathFactory.newInstance().newXPath();
    try {
      XPathExpression titleParser = parser.compile(xpath);
      ByteArrayInputStream byteStream //
          = new ByteArrayInputStream(s.getBytes(MediaWiki.getCharset()));
      InputSource contenido = new InputSource(byteStream);
      return titleParser.evaluate(contenido);
    } catch (XPathExpressionException | UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
