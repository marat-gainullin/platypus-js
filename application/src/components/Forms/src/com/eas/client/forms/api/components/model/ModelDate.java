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

    protected ModelDate(DbDate aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelDate() {
        super();
        setDelegate(new DbDate());
    }

    @ScriptFunction(jsDoc = "Determines if component is editable.")
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
    
    @ScriptFunction(jsDoc = "Sets up the control appearance. If true, than calndar panel is displayed, otherwise date/time combo is displayed.")
    public boolean isExpanded()
    {
        return delegate.isExpanded();
    }
    
    @ScriptFunction
    public void setExpanded(boolean aValue) throws Exception
    {
        delegate.setExpanded(aValue);
        invalidate();
    }
    
    @ScriptFunction
    public String getDateFormat()
    {
        return delegate.getDateFormat();
    }
    
    @ScriptFunction
    public void setDateFormat(String aValue)
    {
        delegate.setDateFormat(aValue);
        invalidate();
    }
}
