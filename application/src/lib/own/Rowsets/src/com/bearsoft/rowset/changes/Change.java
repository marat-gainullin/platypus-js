/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public abstract class Change implements HasPublished {

    public String entityName;
    public boolean consumed;
    //
    
    protected Object published;

    public Change(String aEntityName) {
        super();
        entityName = aEntityName;
    }

    public abstract void accept(ChangeVisitor aChangeVisitor) throws Exception;

    @ScriptFunction(jsDoc = "Indicates the change's type (Insert, Update, Delete or Command).")
    public String getType() {
        return getClass().getSimpleName();
    }

    @ScriptFunction(jsDoc = "Indicates the change's destination entity.")
    public String getEntity() {
        return entityName;
    }

    @Override
    public void setPublished(Object aValue) {
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
