/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.Client;
import com.eas.client.login.PrincipalHost;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptResolver;
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author AB
 */
class ServerScriptResolver implements ScriptResolver {

    private PlatypusServerCore serverCore;

    public ServerScriptResolver(PlatypusServerCore aServerCore) {
        serverCore = aServerCore;
    }

    @Override
    public Object getScript(String aScriptName, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, ScriptResolverHost aScriptResolverHost) {
        if (aScriptName == null || aScriptName.isEmpty()) {
            throw new IllegalArgumentException("Module name is missing. Unnamed server modules are not allowed.");
        }
        // Let's check the module is already created
        ServerScriptRunner serverModule = getServerCore().getSessionManager().getSystemSession().getModule(aScriptName);
        if (serverModule == null) {
            serverModule = getServerCore().getSessionManager().getCurrentSession().getModule(aScriptName);
        }
        if (serverModule == null) {
            try {
                serverModule = new ServerScriptRunner(getServerCore(), getServerCore().getSessionManager().getSystemSession(),
                        new ModuleConfig(false, false, false, null, aScriptName),
                        ScriptUtils.getScope(), getServerCore(), getServerCore(), aScriptResolverHost);
                serverModule.execute();
                // Clients are not allowed to manage modules instances in system session,
                // so we take care only of session modules.
                getServerCore().getSessionManager().getSystemSession().registerModule(serverModule);
                return serverModule;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return serverModule;
        }
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }
}
