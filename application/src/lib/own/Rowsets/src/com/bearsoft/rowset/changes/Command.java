/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class Command extends Change {

    public String command;// transient property
    public Value[] parameters;

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
    public Value[] getParameters() {
        return parameters;
    }
    
}
