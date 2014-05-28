/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;

/**
 * Holds a typed value for some change in a changes log. 
 * @author mg, vv refactoring
 */
public class ChangeValue implements HasPublished {

    public String name;
    public Object value;
    public DataTypeInfo type;

    protected Object published;

    public ChangeValue(String aName, Object aValue, DataTypeInfo aType) {
        name = aName;
        value = aValue;
        type = aType;
    }

    @ScriptFunction(jsDoc = "Name of changed property.")
    public String getName() {
        return name;
    }

    @ScriptFunction(jsDoc = "New value.")
    public Object getValue() {
        return value;
    }

    @Override
    public Object getPublished() {
        return published;
    }

    @Override
    public void setPublished(Object aValue) {
        published = aValue;
    }
}
