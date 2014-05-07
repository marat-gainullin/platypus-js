/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Client;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.metadata.ApplicationElement;
import java.util.HashMap;
import java.util.Map;

/**
 * <code>ScriptDocument</code> cache.
 *
 * @author mg
 */
public abstract class ScriptDocuments {

    protected Client client;
    protected final Map<String, ActualCacheEntry<ScriptDocument>> documents = new HashMap<>();

    public ScriptDocuments(Client aClient) {
        super();
        client = aClient;
    }

    public synchronized void removeScriptDocument(String aAppElement) {
        documents.remove(aAppElement);
    }

    public synchronized void clear() {
        documents.clear();
    }

    public synchronized ScriptDocument getScriptDocument(String aAppElementId) throws Exception {
        ActualCacheEntry<ScriptDocument> scriptDocEntry = documents.get(aAppElementId);
        ScriptDocument scriptDoc = scriptDocEntry != null ? scriptDocEntry.getValue() : null;
        if (scriptDoc == null) {
            final ApplicationElement appElement = client.getAppCache().get(aAppElementId);
            if (appElement != null) {
                scriptDoc = appElement2Document(appElement);
                assert appElement.getId() != null : "Application element with null name detected!";
                documents.put(aAppElementId, new ActualCacheEntry<>(scriptDoc, appElement.getTxtContentLength(), appElement.getTxtCrc32()));
            }
        }
        return scriptDoc;
    }

    protected abstract ScriptDocument appElement2Document(ApplicationElement aAppElement) throws Exception;
}
