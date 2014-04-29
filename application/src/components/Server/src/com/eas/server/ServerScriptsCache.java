/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.script.ScriptUtils;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ServerScriptsCache {

    protected Map<String, JSObject> cache = new HashMap<>();
    protected PlatypusServerCore serverCore;

    public ServerScriptsCache(PlatypusServerCore aServerCore) {
        super();
        serverCore = aServerCore;
    }

    public synchronized JSObject get(String aModuleId) throws Exception {
        // TODO: isActual ?
        JSObject instance = cache.get(aModuleId);
        if (instance == null) {
            instance = ScriptUtils.createModule(aModuleId);
            cache.put(aModuleId, instance);
        }
        return instance;
    }

    public synchronized void clear() {
        cache.clear();
    }
}
