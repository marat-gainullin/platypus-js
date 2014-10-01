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
    protected static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected static DocumentBuilder builder;

    private Source2XmlDom() {
        super();
    }

    public static synchronized Document transform(String source) {
        try {
            if (builder == null) {
                builder = factory.newDocumentBuilder();
            }
            return builder.parse(new InputSource(new StringReader(source)));
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(Source2XmlDom.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }
/*
    public static synchronized Document transform(Reader source) {
        try {
            if (builder != null) {
                return builder.parse(new InputSource(source));
            } else {
                return null;
            }
        } catch (SAXException | IOException ex) {
            Logger.getLogger(Source2XmlDom.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }
    */
}
