/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports.store;

import com.eas.client.metadata.ApplicationElement;
import com.eas.client.reports.ReportDocument;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.ScriptDocument2Dom;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sun.misc.UUEncoder;

/**
 *
 * @author mg
 */
public class ReportDocument2Dom extends ScriptDocument2Dom {

    protected ReportDocument2Dom() throws ParserConfigurationException {
        super();
    }

    public static Document reportDocument2Dom(ReportDocument aDoc) throws Exception {
        ReportDocument2Dom doc2dom = new ReportDocument2Dom();
        return doc2dom.encodeDoc2Dom(aDoc);
    }

    @Override
    protected Document encodeDoc2Dom(ScriptDocument aDoc) throws Exception {
        Document dom = super.encodeDoc2Dom(aDoc);
        Element el = dom.getDocumentElement();
        byte[] template = ((ReportDocument) aDoc).getTemplate();
        if (template != null && template.length > 0) {
            Node templateNode = dom.createElement(ApplicationElement.XLS_LAYOUT_TAG_NAME);
            UUEncoder encoder = new UUEncoder();
            templateNode.setTextContent(encoder.encodeBuffer(template));
            el.appendChild(templateNode);
        }
        return dom;
    }
}
