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
public class Command extends Change {
    
    private static JSObject publisher;
    public String command;// transient property
    public ChangeValue[] parameters;

    public Command(String aEntityId) {
        super(aEntityId);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Command sql text to be applied in a database.")
    public String getCommand() {
        return command;
    }

    @ScriptFunction(jsDoc = "Parameters of command.")
    public ChangeValue[] getParameters() {
        return parameters;
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
