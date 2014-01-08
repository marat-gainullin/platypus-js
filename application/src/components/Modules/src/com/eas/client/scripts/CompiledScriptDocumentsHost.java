/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.metadata.ApplicationElement;

/**
 * Environment contract interface for <code>CompiledScriptDocuments</code>
 * @author mg
 */
public interface CompiledScriptDocumentsHost {

    public CompiledScriptDocuments getDocuments();
    
    public void defineJsClass(String aClassName, ApplicationElement aAppElement);
}
