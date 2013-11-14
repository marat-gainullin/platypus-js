/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.text.DbText;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class ModelTextArea extends ScalarModelComponent<DbText> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A model components for a text area.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelTextArea() {
        super();
        setDelegate(new DbText());
    }

    protected ModelTextArea(DbText aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    private static final String EDITABLE_JSDOC = "/**\n"
            + "* Determines if component is editable.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
}
