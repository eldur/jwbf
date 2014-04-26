package net.sourceforge.jwbf.extractXml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.core.actions.util.ActionException;
import net.sourceforge.jwbf.core.actions.util.ProcessException;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

@Slf4j
public class XmlConverter {

  public static Element getRootElementWithError(String xml) {
    SAXBuilder builder = new SAXBuilder();
    org.jdom.Element root = null;
    try {
      Reader i = new StringReader(xml);
      Document doc = builder.build(new InputSource(i));

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
    return new Element(root);
  }

  public static Element getErrorElement(Element rootElement) {
    Element elem = rootElement.getChild("error");
    if (elem != null) {
      log.error(elem.getAttributeValue("code") + ": " + elem.getAttributeValue("info"));
    }
    return elem;
  }

  public static Element getRootElement(String xml) {
    Element rootElement = getRootElementWithError(xml);
    Element elem = getErrorElement(rootElement);
    if (elem != null) {
      String xmlError = xml;
      if (xmlError.length() > 700) {
        xmlError = xmlError.substring(0, 700);
      }
      throw new ProcessException(xmlError);
    }
    return rootElement;
  }

  public static String evaluateXpath(String s, String xpath) {
    XPath parser = XPathFactory.newInstance().newXPath();
    try {
      XPathExpression titleParser = parser.compile(xpath);
      ByteArrayInputStream byteStream //
      = new ByteArrayInputStream(s.getBytes(MediaWiki.getCharset()));
      InputSource contenido = new InputSource(byteStream);
      return titleParser.evaluate(contenido);
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException(e);
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
