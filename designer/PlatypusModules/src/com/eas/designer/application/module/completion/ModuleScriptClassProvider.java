/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service=ScriptClassProvider.class)
public class ModuleScriptClassProvider implements ScriptClassProvider {

    @Override
    public Class<?> getClassByName(String name) {
        return null;
    }   
}
