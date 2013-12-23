/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.date.DbDate;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class ModelDate extends ScalarModelComponent<DbDate> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a date. \n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelDate() {
        super();
        setDelegate(new DbDate());
    }

    protected ModelDate(DbDate aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + " * Determines if component is editable.\n"
            + " */";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
    private static final String EXPANDED_JSDOC = ""
            + "/**\n"
            + "* Sets up the control appearance. If true, than calndar panel is displayed, otherwise date/time combo is displayed.\n"
            + "*/";

    @ScriptFunction(jsDoc = EXPANDED_JSDOC)
    public boolean getExpanded() {
        return delegate.isExpanded();
    }

    @ScriptFunction
    public void setExpanded(boolean aValue) throws Exception {
        delegate.setExpanded(aValue);
        invalidate();
    }

    @ScriptFunction
    public String getDateFormat() {
        return delegate.getDateFormat();
    }

    @ScriptFunction
    public void setDateFormat(String aValue) {
        delegate.setDateFormat(aValue);
        invalidate();
    }
    
    @ScriptFunction
    public String getEmptyText() {
        return delegate.getEmptyText();
    }

    @ScriptFunction
    public void setEmptyText(String aValue) {
        delegate.setEmptyText(aValue);
    }

}
