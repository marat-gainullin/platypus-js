/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public abstract class Change implements HasPublished {

    public String entityName;
    public boolean consumed;
    //
    protected JSObject published;

    public Change(String aEntityName) {
        super();
        entityName = aEntityName;
    }

    public abstract void accept(ChangeVisitor aChangeVisitor) throws Exception;

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Indicates the change's type (Insert, Update, Delete or Command).\n"
            + " */")
    public String getType() {
        return getClass().getSimpleName();
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " *Indicates the change's destination entity.\n"
            + " */")
    public String getEntity() {
        return entityName;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    @Override
    public String toString() {
        return getType();
    }
}
