/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.store;

import com.eas.client.forms.FormDocument;
import com.eas.client.metadata.ApplicationElement;
import com.eas.store.Object2Dom;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author mg
 */
public class FormDocument2Dom
{
    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

    protected FormDocument2Dom() throws ParserConfigurationException
    {
        documentBuilder = builderFactory.newDocumentBuilder();
    }

    public static Document scriptDocument2Dom(FormDocument aDoc) throws Exception
    {
        FormDocument2Dom doc2Dom = new FormDocument2Dom();
        return doc2Dom.encodeDoc2Dom(aDoc);
    }

    protected Document encodeDoc2Dom(FormDocument aDoc) throws Exception
    {
        Document dom = documentBuilder.newDocument();
	dom.setXmlStandalone(true);
        Node root = dom.createElement(ApplicationElement.FORM_ROOT_TAG_NAME);
        dom.appendChild(root);
        Node source = dom.createElement(ApplicationElement.SCRIPT_SOURCE_TAG_NAME);
        root.appendChild(source);

        Document dmDom = aDoc.getModel().toXML();
        /*
        NodeList dmRoots = dmDom.getElementsByTagName(Model2XmlDom.DATAMODEL_TAG_NAME);
        if (dmRoots.getLength() > 0)
            root.appendChild(dom.adoptNode(dmRoots.item(0)));
         */
        root.appendChild(dom.adoptNode(dmDom.getDocumentElement()));

        source.setTextContent(aDoc.getScriptSource());

        Document formLayoutDom = Object2Dom.transform(aDoc.getFormDesignInfo(), ApplicationElement.LAYOUT_TAG_NAME);
        root.appendChild(dom.adoptNode(formLayoutDom.getDocumentElement()));

        return dom;
    }

}
