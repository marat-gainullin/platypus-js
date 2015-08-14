/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public interface HasJsName {

    public static final String JS_NAME_DOC = ""
            + "/**\n"
            + " * Gets name of this component.\n"
            + " */";

    @ScriptFunction(jsDoc = JS_NAME_DOC)
    public String getName();
    
    @ScriptFunction
    public void setName(String aValue);
}
