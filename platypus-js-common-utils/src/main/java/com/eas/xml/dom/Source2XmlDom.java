package com.eas.xml.dom;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author mg
 */
public class Source2XmlDom {

    private Source2XmlDom() {
        super();
    }

    public static Document transform(String source) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        } catch (ParserConfigurationException ex) {
            // no op. unsupported features it is normal.
        }
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(source)));
    }
}
