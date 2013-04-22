/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.store;

import com.eas.client.Client;
import com.eas.client.forms.DbFormDesignInfo;
import com.eas.client.forms.FormDocument;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import com.eas.store.Object2Dom;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class Dom2FormDocument extends Dom2ScriptDocument {

    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

    protected Dom2FormDocument() throws ParserConfigurationException {
        documentBuilder = builderFactory.newDocumentBuilder();
    }

    public static FormDocument dom2FormDocument(Client aClient, Document aDom) throws Exception {
        Dom2FormDocument domReader = new Dom2FormDocument();
        return domReader.parseDom(aClient, aDom);
    }

    @Override
    protected String rootTagName() {
        return ApplicationElement.FORM_ROOT_TAG_NAME;
    }

    @Override
    public FormDocument parseDom(Client aClient, Document aDom) throws Exception {
        ScriptDocument superDoc = super.parseDom(aClient, aDom);
        DbFormDesignInfo formLayout = null;
        NodeList roots = aDom.getChildNodes();
        for (int i = 0; i < roots.getLength(); i++) {
            Node root = roots.item(i);
            if (ApplicationElement.FORM_ROOT_TAG_NAME.equals(root.getNodeName())) {
                NodeList tags = root.getChildNodes();
                for (int j = 0; j < tags.getLength(); j++) {
                    Node tag = tags.item(j);
                    switch (tag.getNodeName()) {
                        case ApplicationElement.LAYOUT_TAG_NAME:
                            Document layoutDom = documentBuilder.newDocument();
			    layoutDom.setXmlStandalone(true);
                            layoutDom.appendChild(layoutDom.importNode(tag, true));
                            formLayout = new DbFormDesignInfo();
                            Object2Dom.transform(formLayout, layoutDom);
                            break;
                    }
                }
            }
        }
        return new FormDocument(superDoc.getModel(), superDoc.getScriptSource(), formLayout);
    }
}
