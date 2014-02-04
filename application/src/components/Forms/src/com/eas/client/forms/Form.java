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
            + " * The array of application's shown forms.\n"
            + " */";
    
    @ScriptFunction(jsDoc = SHOWN_JSDOC)
    public Form[] getShown() {
        return null;
    }
    
    private static final String SHOWN_FORM_JSDOC = ""
            + "/**\n"
            + " * Gets a shown form by its key.\n"
            + " * @param a form key identifier"
            + " * @return a form from the open forms registry"
            + " */";
    
    @ScriptFunction(jsDoc = SHOWN_FORM_JSDOC, params = {"key"})
    public Form getShownForm() {
        return null;
    }
    
    private static final String ON_CHANGE_JSDOC = ""
            + "/**\n"
            + " * The shown forms registry change event handler function.\n"
            + " */";
    @ScriptFunction(jsDoc = ON_CHANGE_JSDOC)
    public Function getOnChange() {
        return null;
    }

    public void setOnChange(Function aValue) {
    }
    
}
