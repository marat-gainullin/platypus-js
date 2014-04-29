/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.metadata.ApplicationElement;
import com.eas.script.ScriptUtils;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ScriptsCache {
    
    protected Map<String, JSObject> cache = new HashMap<>();
    protected PlatypusClientApplication app;

    public ScriptsCache(PlatypusClientApplication aApp) {
        super();
        app = aApp;
    }

    public synchronized JSObject get(String aModuleId) throws Exception {        
        JSObject instance = cache.get(aModuleId);
        ApplicationElement appElement = app.getClient().getAppCache().get(aModuleId);
        if(instance != null && !app.getClient().getAppCache().isActual(aModuleId, appElement.getTxtContentLength(), appElement.getTxtCrc32())){
            instance = null;
            cache.remove(aModuleId);
        }
        if (instance == null) {
            instance = ScriptUtils.createModule(aModuleId);
            //runner = new ScriptRunner(aModuleId, app.getClient(), ScriptRunner.initializePlatypusStandardLibScope(), app, app, new Object[]{});
            cache.put(aModuleId, instance);
        }
        return instance;
    }

    public synchronized void clear() {
        cache.clear();
    }
}
