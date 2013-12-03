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
public class Update extends Change {

    public Value[] keys;
    public Value[] data;

    public Update(String aEntityId) {
        super(aEntityId);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Keys used for indentifying data changes within a target datasource")
    public Value[] getKeys() {
        return keys;
    }

    @ScriptFunction(jsDoc = "Data to be applied within a target datasource")
    public Value[] getData() {
        return data;
    }
}
