/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.internal.runtime.PropertyAccess;

/**
 *
 * @author mg
 */
public class ScriptsAutoLoadBindings extends HashMap<String, Object> implements Bindings {

    public static final String APP_ELEMENT_NOT_FOUND_MSG = "Application element with id %s not found.";
    protected ScriptDocumentsHost documentsHost;
    protected Client client;
    /**
     * Current principal provider
     */
    protected PrincipalHost principalHost;

    public ScriptsAutoLoadBindings(Client aClient, ScriptDocumentsHost aDocumentsHost, PrincipalHost aPrincipalHost) {
        super();
        client = aClient;
        documentsHost = aDocumentsHost;
        principalHost = aPrincipalHost;
    }

    @Override
    public Object get(Object key) {
        String name = (String) key;
        if (name != null && !name.isEmpty()) {
            try {
                Object jsGlobal = super.get(NashornScriptEngine.NASHORN_GLOBAL);
                if (jsGlobal instanceof PropertyAccess) {
                    PropertyAccess pa = (PropertyAccess) jsGlobal;
                    if (!pa.has(key)) {
                        ApplicationElement moduleCandidate = client.getAppCache().get(name);
                        if (moduleCandidate != null
                                && (moduleCandidate.getType() == ClientConstants.ET_COMPONENT
                                || moduleCandidate.getType() == ClientConstants.ET_FORM
                                || moduleCandidate.getType() == ClientConstants.ET_REPORT)) {
                            PlatypusScriptedResource.executeScriptResource(name);
                            assert pa.has(key);
                            Object justExecuted = pa.get(key);
                            assert justExecuted instanceof JSObject;
                            JSObject jsConstructor = (JSObject) justExecuted;
                            assert jsConstructor.isFunction();
                            ScriptDocument scriptDoc = documentsHost.getDocuments().getScriptDocument(name);
                            if (scriptDoc == null) {
                                throw new NullPointerException(String.format(APP_ELEMENT_NOT_FOUND_MSG, name));
                            }
                            SecuredJSConstructor securedConstructor = new SecuredJSConstructor(jsConstructor, name, scriptDoc.getModuleAllowedRoles(), scriptDoc.getPropertyAllowedRoles(), principalHost);
                            pa.set(key, securedConstructor, true);
                            return justExecuted;
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ScriptsAutoLoadBindings.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.get(key);
    }

}
