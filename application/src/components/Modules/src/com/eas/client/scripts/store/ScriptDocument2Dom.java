/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts.store;

import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.scripts.ScriptDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author pk
 */
public class ScriptDocument2Dom {

    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

    protected ScriptDocument2Dom() throws ParserConfigurationException {
        documentBuilder = builderFactory.newDocumentBuilder();
    }

    public static Document scriptDocument2Dom(ScriptDocument aDoc) throws Exception {
        ScriptDocument2Dom doc2Dom = new ScriptDocument2Dom();
        return doc2Dom.encodeDoc2Dom(aDoc);
    }

    protected Document encodeDoc2Dom(ScriptDocument aDoc) throws Exception {
        Document dom = documentBuilder.newDocument();
	dom.setXmlStandalone(true);
        Node root = dom.createElement(ApplicationElement.SCRIPT_ROOT_TAG_NAME);
        dom.appendChild(root);
        Node source = dom.createElement(ApplicationElement.SCRIPT_SOURCE_TAG_NAME);
        root.appendChild(source);

        Document dmDom = aDoc.getModel().toXML();
        NodeList dmRoots = dmDom.getElementsByTagName(Model2XmlDom.DATAMODEL_TAG_NAME);
        if (dmRoots.getLength() > 0) {
            root.appendChild(dom.adoptNode(dmRoots.item(0)));
        }
        source.setTextContent(aDoc.getScriptSource());
        return dom;
    }
}
