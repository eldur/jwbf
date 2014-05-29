package net.sourceforge.jwbf.mapper;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.google.common.base.Charsets;
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

  public static XmlElement getRootElementWithError(String xml) {
    SAXBuilder builder = new SAXBuilder();
    org.jdom.Element root = null;
    try {
      Document doc = builder.build(new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)));

      root = doc.getRootElement();

    } catch (JDOMException e) {
      log.error(xml);
      throw new IllegalArgumentException(e);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    if (root == null) {
      throw new ActionException("no root element found");
    }
    return new XmlElement(root);
  }

  public static XmlElement getErrorElement(XmlElement rootXmlElement) {
    XmlElement elem = rootXmlElement.getChild("error");
    if (elem != null) {
      log.error(elem.getAttributeValue("code") + ": " + elem.getAttributeValue("info"));
    }
    return elem;
  }

  public static XmlElement getRootElement(String xml) {
    XmlElement rootXmlElement = getRootElementWithError(xml);
    XmlElement elem = getErrorElement(rootXmlElement);
    if (elem != null) {
      String xmlError = xml;
      if (xmlError.length() > 700) {
        xmlError = xmlError.substring(0, 700);
      }
      throw new ProcessException(xmlError);
    }
    return rootXmlElement;
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
