/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports.store;

import com.eas.client.Client;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.reports.ReportDocument;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.UUDecoder;

/**
 *
 * @author mg
 */
public class Dom2ReportDocument extends Dom2ScriptDocument {

    protected Dom2ReportDocument() throws ParserConfigurationException {
        super();
    }

    public static ReportDocument dom2ReportDocument(Client aClient, Document aDom) throws Exception {
        Dom2ReportDocument dom2doc = new Dom2ReportDocument();
        return (ReportDocument) dom2doc.parseDom(aClient, aDom);
    }

    @Override
    protected String rootTagName() {
        return ApplicationElement.SCRIPT_ROOT_TAG_NAME;
    }

    @Override
    public ScriptDocument parseDom(Client aClient, Document aDom) throws Exception {
        ScriptDocument superDoc = super.parseDom(aClient, aDom);
        byte[] template = null;
        NodeList roots = aDom.getChildNodes();
        for (int i = 0; i < roots.getLength(); i++) {
            Node root = roots.item(i);
            if (rootTagName().equals(root.getNodeName())) {
                NodeList tags = root.getChildNodes();
                for (int j = 0; j < tags.getLength(); j++) {
                    Node child = tags.item(j);
                    if (ApplicationElement.XLS_LAYOUT_TAG_NAME.equals(child.getNodeName())) {
                        String strContent = child.getTextContent();
                        if (strContent != null && !strContent.isEmpty()) {
                            UUDecoder decoder = new UUDecoder();
                            template = decoder.decodeBuffer(strContent);
                        }
                    }
                }
            }
        }
        ReportDocument doc = new ReportDocument(template, superDoc.getModel(), superDoc.getScriptSource());
        return doc;
    }
}
