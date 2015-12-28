/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Holds a typed value for some change in a changes log. 
 * @author mg, vv
 */
public class ChangeValue implements HasPublished {

    public String name;
    public Object value;

    private JSObject published;

    public ChangeValue(String aName, Object aValue) {
        name = aName;
        value = Scripts.getSpace() != null ? Scripts.getSpace().toJava(aValue) : aValue;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Name of changed property.\n"
            + " */")
    public String getName() {
        return name;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Value of changed property.\n"
            + " */")
    public Object getValue() {
        return Scripts.getSpace().toJs(value);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
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
}
