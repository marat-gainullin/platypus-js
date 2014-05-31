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
public class Insert extends Change {

    public ChangeValue[] data;

    public Insert(String aEntityId) {
        super(aEntityId);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Data that will be inserted.")
    public ChangeValue[] getData() {
        return data;
    }
}
