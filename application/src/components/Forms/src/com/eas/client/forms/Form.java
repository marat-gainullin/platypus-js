/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.script.ScriptFunction;
import org.mozilla.javascript.Function;

/**
 * This class holds information of the script properties of a Form object
 * @author vv
 */
public class Form {
    
    private static final String SHOWN_JSDOC = ""
            + "/**\n"
            + "* The form's show and hide event handler function.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = SHOWN_JSDOC)
    public Form[] getShown() {
        return null;
    }
    
    private static final String ON_CHANGE_JSDOC = ""
            + "/**\n"
            + "* The form's show and hide event handler function.\n"
            + "*/";
    @ScriptFunction(jsDoc = ON_CHANGE_JSDOC)
    public Function getOnChange() {
        return null;
    }

    public void setOnChange(Function aValue) {
    }
    
}
