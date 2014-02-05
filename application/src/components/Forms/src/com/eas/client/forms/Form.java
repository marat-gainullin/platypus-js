/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import org.mozilla.javascript.Function;

/**
 * This class holds information of the script properties of a Form object
 * @author vv
 */
@ScriptObj(name = "Form", jsDoc = "/**\n"
        + "* Application form.\n"
        + "*/")
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
            + " * @param key a form key identifier\n"
            + " * @return a form from the open forms registry\n"
            + " */";
    
    @ScriptFunction(jsDoc = SHOWN_FORM_JSDOC, params = {"key"})
    public Form getShownForm(String aKey) {
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
