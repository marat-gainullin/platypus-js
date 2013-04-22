/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.Client;
import com.eas.client.login.PrincipalHost;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author AB
 */
public interface ScriptResolver {
    /**
     * 
     * @param aScriptName 
     * @param aClient 
     * @param aScope 
     * @param aPrincipalHost 
     * @param aCompiledScriptDocumentsHost 
     * @param aScriptResolverHost 
     * @return ScriptRunner or ServerScriptRunner. 
     */
    public Object getScript(String aScriptName, Client aClient, Scriptable aScope, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aCompiledScriptDocumentsHost, ScriptResolverHost aScriptResolverHost);
    
}
