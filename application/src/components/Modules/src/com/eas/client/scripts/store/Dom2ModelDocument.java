package com.eas.client.scripts.store;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationPlatypusModel;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.PlatypusClient;
import com.eas.xml.dom.Source2XmlDom;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author pk, mg refactoring
 */
public class Dom2ModelDocument {

    public static final String BAD_ROOT_TAGS_MSG = "Should be only one root tag in platypus documents";

    public static ApplicationModel<?, ?, ?, ?> load(Client aClient, String aAppElementName) throws Exception {
        ApplicationElement appElement = aClient.getAppCache().get(aAppElementName);
        if (appElement.getType() == ClientConstants.ET_RESOURCE) {
            Document doc = Source2XmlDom.transform(new String(appElement.getBinaryContent(), SettingsConstants.COMMON_ENCODING));
            return parseTag(aClient, doc.getDocumentElement());
        } else {
            return transform(aClient, appElement.getContent());
        }
    }

    public static ApplicationModel<?, ?, ?, ?> transform(Client aClient, Document aDocument) throws Exception {
        Element root = aDocument.getDocumentElement();
        NodeList tags = root.getChildNodes();
        for (int j = 0; j < tags.getLength(); j++) {
            Node node = tags.item(j);
            if (node instanceof Element && Model2XmlDom.DATAMODEL_TAG_NAME.equals(((Element) node).getTagName())) {
                return parseTag(aClient, (Element) node);
            }
        }
        return null;
    }

    protected Dom2ModelDocument() throws ParserConfigurationException {
        super();
    }

    protected static ApplicationModel<?, ?, ?, ?> parseTag(Client aClient, Element aTag) {
        if (aClient instanceof PlatypusClient) {
            ApplicationPlatypusModel pModel = new ApplicationPlatypusModel((PlatypusClient) aClient);
            pModel.accept(new XmlDom2ApplicationModel<>(aTag));
            return pModel;
        } else if (aClient instanceof DbClient) {
            ApplicationDbModel dbModel = new ApplicationDbModel((DbClient) aClient);
            dbModel.accept(new XmlDom2ApplicationModel<>(aTag));
            return dbModel;
        } else {
            assert false : "Unknown type of client found, while creating an application model";
            return null;
        }
    }
}
