/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client;

import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public interface ModulesProxy {
    
    public ModuleStructure getModule(String aName, Consumer<ModuleStructure> onSuccess, Consumer<Exception> onFailure) throws Exception;
    
    public String getLocalPath();
    
    public AppElementFiles nameToFiles(String aName) throws Exception;
}
