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

    public ModelTextArea(DbText aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelTextArea() {
        super();
        setDelegate(new DbText());
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
