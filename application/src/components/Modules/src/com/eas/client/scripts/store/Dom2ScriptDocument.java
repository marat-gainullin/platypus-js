/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts.store;

import com.eas.client.Client;
import com.eas.client.DbClient;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationPlatypusEntity;
import com.eas.client.model.application.ApplicationPlatypusModel;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.threetier.PlatypusClient;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author pk, mg refactoring
 */
public class Dom2ScriptDocument {

    public static final String BAD_ROOT_TAGS_MSG = "In platypus documents should be only one root tag";
    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

    public static ScriptDocument dom2ScriptDocument(Client aClient, Document aDom) throws Exception {
        Dom2ScriptDocument domReader = new Dom2ScriptDocument();
        return domReader.parseDom(aClient, aDom);
    }

    protected Dom2ScriptDocument() throws ParserConfigurationException {
        documentBuilder = builderFactory.newDocumentBuilder();
    }

    protected String rootTagName() {
        return ApplicationElement.SCRIPT_ROOT_TAG_NAME;
    }

    public ScriptDocument parseDom(Client aClient, Document aDom) throws Exception {
        String scriptSource = null;
        ApplicationModel<?, ?, ?, ?> model = null;
        NodeList roots = aDom.getChildNodes();
        if (roots.getLength() != 1) {
            throw new Exception(BAD_ROOT_TAGS_MSG);
        }
        Node root = roots.item(0);
        NodeList tags = root.getChildNodes();
        for (int j = 0; j < tags.getLength(); j++) {
            Node tag = tags.item(j);
            switch (tag.getNodeName()) {
                case ApplicationElement.SCRIPT_SOURCE_TAG_NAME:
                    scriptSource = tag.getTextContent();
                    break;
                case Model2XmlDom.DATAMODEL_TAG_NAME:
                    Document dmDom = documentBuilder.newDocument();
                    dmDom.appendChild(dmDom.importNode(tag, true));
                    if (aClient instanceof PlatypusClient) {
                        ApplicationPlatypusModel pModel = new ApplicationPlatypusModel((PlatypusClient) aClient);
                        pModel.accept(new XmlDom2ApplicationModel<ApplicationPlatypusEntity>(dmDom));
                        model = pModel;
                    } else if (aClient instanceof DbClient) {
                        ApplicationDbModel dbModel = new ApplicationDbModel((DbClient) aClient);
                        dbModel.accept(new XmlDom2ApplicationModel<ApplicationDbEntity>(dmDom));
                        model = dbModel;
                    } else {
                        assert false : "Unknown type of client found, while creating an application model";
                    }
                    break;
            }
        }
        return new ScriptDocument(model, scriptSource);
    }
}
