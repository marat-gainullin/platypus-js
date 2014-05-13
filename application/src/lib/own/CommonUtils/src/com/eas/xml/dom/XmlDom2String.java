/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.xml.dom;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class XmlDom2String {

    protected static ThreadLocal<TransformerFactory> factories = new ThreadLocal<>();

    private XmlDom2String() {
        super();
    }

    public static String transform(Document doc) {
        try {
            if (doc != null) {
                TransformerFactory tfactory = factories.get();
                if (tfactory == null) {
                    tfactory = TransformerFactory.newInstance();
                    factories.set(tfactory);
                }
                Transformer transformer = tfactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                StringWriter sw = new StringWriter();
                StreamResult res = new StreamResult(sw);
                transformer.transform(new DOMSource(doc), res);
                return sw.toString();
            }
        } catch (IllegalArgumentException | TransformerException ex) {
            Logger.getLogger(XmlDom2String.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
