/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.xml.dom;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    // setup documents framework
    private Source2XmlDom() {
        super();
    }

    public static Document transform(String source) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(source)));
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(Source2XmlDom.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }
}
