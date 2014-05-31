/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports.store;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.reports.ReportTemplate;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.UUDecoder;

/**
 *
 * @author mg
 */
public class Dom2ReportDocument {

    protected Dom2ReportDocument() throws ParserConfigurationException {
        super();
    }

    public static ReportTemplate load(Client aClient, String aAppElementName, ApplicationModel<?, ?, ?, ?> aModel) throws Exception {
        ApplicationElement appElement = aClient.getAppCache().get(aAppElementName);
        if (appElement.getType() == ClientConstants.ET_RESOURCE) {
            String format = aAppElementName.substring(aAppElementName.lastIndexOf('.') + 1, aAppElementName.length());
            return new ReportTemplate(appElement.getBinaryContent(), aModel, format, aAppElementName);
        } else {
            return transform(appElement.getContent(), aModel);
        }
    }

    public static ReportTemplate transform(Document aDocument, ApplicationModel<?, ?, ?, ?> aModel) throws Exception {
        Dom2ReportDocument dom2doc = new Dom2ReportDocument();
        return dom2doc.parseDom(aDocument, aModel);
    }

    protected ReportTemplate parseDom(Document aDocument, ApplicationModel<?, ?, ?, ?> aModel) throws Exception {
        byte[] template = null;
        String format = null;
        Element root = aDocument.getDocumentElement();
        NodeList tags = root.getChildNodes();
        for (int j = 0; j < tags.getLength(); j++) {
            Node node = tags.item(j);
            if (node instanceof Element && ApplicationElement.XLS_LAYOUT_TAG_NAME.equals(((Element) node).getTagName())) {
                format = node.getAttributes().getNamedItem(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME) != null ? node.getAttributes().getNamedItem(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME).getNodeValue() : PlatypusFiles.REPORT_LAYOUT_EXTENSION;
                String strContent = node.getTextContent();
                if (strContent != null && !strContent.isEmpty()) {
                    UUDecoder decoder = new UUDecoder();
                    template = decoder.decodeBuffer(strContent);
                }
            }
        }
        return new ReportTemplate(template, aModel, format, "");
    }
}
