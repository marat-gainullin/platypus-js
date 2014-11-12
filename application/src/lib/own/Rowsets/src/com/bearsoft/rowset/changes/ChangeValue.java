/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Holds a typed value for some change in a changes log. 
 * @author mg, vv
 */
public class ChangeValue implements HasPublished {

    public String name;
    public Object value;
    public DataTypeInfo type;

    private static JSObject publisher;
    private JSObject published;

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
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
