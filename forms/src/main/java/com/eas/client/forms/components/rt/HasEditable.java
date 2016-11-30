/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import com.eas.script.ScriptFunction;

/**
 *
 * @author Марат
 */
public interface HasEditable {
    
    public static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + " * Determines whether this component is enabled. An enabled component can respond to user input and generate events. Components are enabled initially by default.\n"
            + " */";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable();

    @ScriptFunction
    public void setEditable(boolean aValue);

}
