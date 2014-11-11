/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Delete extends Change {
    
    private static JSObject publisher;
    public ChangeValue[] keys;

    public Delete(String aEntityName) {
        super(aEntityName);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Keys values used for identification of deleted data.")
    public ChangeValue[] getKeys() {
        return keys;
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
    
    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
