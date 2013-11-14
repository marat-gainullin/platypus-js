/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Client;
import com.eas.client.cache.ActualCacheEntry;
import com.eas.client.metadata.ApplicationElement;
import com.eas.script.ScriptUtils;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public abstract class CompiledScriptDocuments {

    protected Client client;
    protected final Map<String, ActualCacheEntry<ScriptDocument>> compiledDocuments = new HashMap<>();

    public CompiledScriptDocuments(Client aClient) {
        client = aClient;
    }

    public synchronized void removeCompiledScriptDocument(String aAppElement) {
        compiledDocuments.remove(aAppElement);
    }

    public synchronized void clearCompiledScriptDocuments() {
        compiledDocuments.clear();
    }
    
    public synchronized ScriptDocument compileScriptDocument(String aAppElementId) throws Exception {
        ActualCacheEntry<ScriptDocument> scriptDocEntry = compiledDocuments.get(aAppElementId);
        ScriptDocument scriptDoc = scriptDocEntry != null ? scriptDocEntry.getValue() : null;
        if (scriptDocEntry != null && scriptDoc != null && !client.getAppCache().isActual(aAppElementId, scriptDocEntry.getTxtContentSize(), scriptDocEntry.getTxtContentCrc32())) {
            scriptDoc = null;
            compiledDocuments.remove(aAppElementId);
            client.getAppCache().remove(aAppElementId);
        }
        if (scriptDoc == null) {
            ApplicationElement appElement = client.getAppCache().get(aAppElementId);
            if (appElement != null) {
                scriptDoc = appElement2Document(appElement);
                Context cx = ScriptUtils.enterContext();
                try {
                    assert appElement.getId() != null : "Application element with null id occured!";
                    /**
                     * TODO: check update of rhino. May be it would be able to
                     * use bytecode generation. if (currentContext.getDebugger()
                     * == null) { currentContext.setOptimizationLevel(5); }else
                     * currentContext.setOptimizationLevel(-1);
                     */
                    cx.setOptimizationLevel(-1);
                    Function compiledFunc = cx.compileFunction(ScriptRunner.checkStandardObjects(cx), "function "+appElement.getId()+"(){"+scriptDoc.getScriptSource()+"; this[\""+ScriptUtils.HANDLERS_PROP_NAME+"\"]="+scriptDoc.generateTopLevelNamedFunctionsContainer()+";}", appElement.getId(), 0, null);
                    scriptDoc.setFunction(compiledFunc);
                } finally {
                    Context.exit();
                }
                compiledDocuments.put(aAppElementId, new ActualCacheEntry<>(scriptDoc, appElement.getTxtContentLength(), appElement.getTxtCrc32()));
            }
        }
        return scriptDoc;
    }

    protected abstract ScriptDocument appElement2Document(ApplicationElement aAppElement) throws Exception;
}
