/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.forms.store.Dom2FormDocument;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.reports.store.Dom2ReportDocument;
import com.eas.client.scripts.ScriptDocuments;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import org.w3c.dom.Document;

/**
 *
 * @author mg
 */
public class ClientScriptDocuments extends ScriptDocuments {

    public ClientScriptDocuments(Client aClient) {
        super(aClient);
    }

    @Override
    protected ScriptDocument appElement2Document(ApplicationElement aAppElement) throws Exception {
        Document doc = aAppElement.getContent();
        ScriptDocument scriptDoc = null;
        switch (aAppElement.getType()) {
            case ClientConstants.ET_COMPONENT:
                scriptDoc = Dom2ScriptDocument.dom2ScriptDocument(client, doc);
                break;
            case ClientConstants.ET_REPORT:
                scriptDoc = Dom2ReportDocument.dom2ReportDocument(client, doc);
                break;
            case ClientConstants.ET_FORM:
                scriptDoc = Dom2FormDocument.dom2FormDocument(client, doc);
                break;
            default:
                throw new Exception(String.format("Application element of unknown type found, The type is %d", aAppElement.getType()));
        }
        assert scriptDoc != null;
        scriptDoc.setEntityId(aAppElement.getId());
        scriptDoc.setTitle(aAppElement.getName());
        scriptDoc.setTxtContentLength(aAppElement.getTxtContentLength());
        scriptDoc.setTxtCrc32(aAppElement.getTxtCrc32());
        scriptDoc.readScriptAnnotations();
        return scriptDoc;
    }
}
