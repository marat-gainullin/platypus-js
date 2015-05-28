/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.util.ArrayList;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Command extends Change {
    
    public String command;// transient property
    private final List<ChangeValue> parameters = new ArrayList<>();

    public Command(String aEntityName) {
        super(aEntityName);
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
    public List<ChangeValue> getParameters() {
        return parameters;
    }
    
    @Override
    public JSObject getPublished() {
        return published;
    }
    
}
