/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.scripts.ScriptRunner;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 */
public class ServerScriptsCache {

    protected Map<String, ServerScriptRunner> cache = new HashMap<>();
    protected PlatypusServerCore serverCore;

    public ServerScriptsCache(PlatypusServerCore aServerCore) {
        super();
        serverCore = aServerCore;
    }

    public synchronized ServerScriptRunner get(String aModuleId) throws Exception {
        ServerScriptRunner runner = cache.get(aModuleId);
        if (runner != null && !serverCore.getDatabasesClient().getAppCache().isActual(runner.getApplicationElementId(), runner.getTxtContentLength(), runner.getTxtCrc32())) {
            runner = null;
            cache.remove(aModuleId);
            serverCore.getDatabasesClient().getAppCache().remove(aModuleId);
        }
        if (runner == null) {
            runner = new ServerScriptRunner(serverCore,
                    serverCore.getSessionManager().getSystemSession(),
                    aModuleId, ScriptRunner.initializePlatypusStandardLibScope(),
                    serverCore,
                    serverCore,
                    new Object[]{});
            runner.setPrototype(ServerScriptRunnerPrototype.getInstance());
            runner.execute();
            cache.put(aModuleId, runner);
        }
        return runner;
    }

    public synchronized void clear() {
        cache.clear();
    }
}
