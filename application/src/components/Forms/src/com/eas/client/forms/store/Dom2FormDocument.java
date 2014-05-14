package com.eas.client.forms.store;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.forms.DbFormDesignInfo;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.settings.SettingsConstants;
import com.eas.controls.FormDesignInfo;
import com.eas.store.Object2Dom;
import com.eas.xml.dom.Source2XmlDom;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class Dom2FormDocument {

    protected Dom2FormDocument() throws ParserConfigurationException {
        super();
    }

    public static FormDesignInfo load(Client aClient, String aAppElementName) throws Exception {
        ApplicationElement appElement = aClient.getAppCache().get(aAppElementName);
        if (appElement.getType() == ClientConstants.ET_RESOURCE) {
            Document doc = Source2XmlDom.transform(new String(appElement.getBinaryContent(), SettingsConstants.COMMON_ENCODING));
            return parseTag(doc.getDocumentElement());
        } else {
            return transform(appElement.getContent());
        }
    }

    public static FormDesignInfo transform(Document aDocument) throws Exception {
        Element root = aDocument.getDocumentElement();
        NodeList tags = root.getChildNodes();
        for (int j = 0; j < tags.getLength(); j++) {
            Node node = tags.item(j);
            if (node instanceof Element && ApplicationElement.LAYOUT_TAG_NAME.equals(((Element) node).getTagName())) {
                return parseTag((Element) node);
            }
        }
        return null;
    }

    protected static FormDesignInfo parseTag(Element aTag) {
        DbFormDesignInfo formLayout = new DbFormDesignInfo();
        Object2Dom.transform(formLayout, aTag);
        return formLayout;
    }
}
