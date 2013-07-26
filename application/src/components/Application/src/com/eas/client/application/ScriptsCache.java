/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.scripts.ScriptRunner;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 */
public class ScriptsCache {
    
    protected Map<String, ScriptRunner> cache = new HashMap<>();
    protected PlatypusClientApplication app;

    public ScriptsCache(PlatypusClientApplication aApp) {
        super();
        app = aApp;
    }

    public synchronized ScriptRunner get(String aModuleId) throws Exception {        
        ScriptRunner runner = cache.get(aModuleId);
        if(runner != null && !app.getClient().getAppCache().isActual(runner.getApplicationElementId(), runner.getTxtContentLength(), runner.getTxtCrc32())){
            runner = null;
            cache.remove(aModuleId);
            app.getClient().getAppCache().remove(aModuleId);
        }
        if (runner == null) {
            runner = new ScriptRunner(aModuleId, app.getClient(), ScriptRunner.initializePlatypusStandardLibScope(), app, app, new Object[]{});
            cache.put(aModuleId, runner);
        }
        return runner;
    }

    public synchronized void clear() {
        cache.clear();
    }
}
