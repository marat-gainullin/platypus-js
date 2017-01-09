/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.Set;

/**
 *
 * @author mg
 */
public class ServerModuleInfo {

    private String moduleName;
    private Set<String> functionsNames;
    private boolean permitted;

    public ServerModuleInfo(String aModuleName, Set<String> aFunctionsNames, boolean aPermitted) {
        super();
        moduleName = aModuleName;
        functionsNames = aFunctionsNames;
        permitted = aPermitted;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String aValue) {
        moduleName = aValue;
    }

    public Set<String> getFunctionsNames() {
        return functionsNames;
    }

    public void setFunctionsNames(Set<String> aFuntionNames) {
        functionsNames = aFuntionNames;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean aPermitted) {
        permitted = aPermitted;
    }

}
