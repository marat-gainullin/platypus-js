/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.check.DbCheck;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class ModelCheckBox extends ScalarModelComponent<DbCheck> {

    protected ModelCheckBox(DbCheck aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelCheckBox() throws Exception {
        super();
        setDelegate(new DbCheck());
    }

    public ModelCheckBox(String aText) throws Exception {
        this();
        delegate.setText(aText);
    }

    @ScriptFunction(jsDoc = "Text of the component.")
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDoc = "Determines if component is editable.")
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
}
