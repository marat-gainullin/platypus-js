/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.Client;
import com.eas.client.login.PrincipalHost;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptResolver;
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author AB
 */
public class ClientScriptResolver implements ScriptResolver {

    @Override
    public Object getScript(String aScriptName, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, ScriptResolverHost aScriptResolverHost) {
        try {
            return new ScriptRunner(aScriptName, aClient, ScriptUtils.getScope(), aPrincipalHost, aCompiledScriptDocumentsHost, aScriptResolverHost);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
