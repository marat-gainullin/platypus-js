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
public class Delete extends Change {

    public ChangeValue[] keys;

    public Delete(String aEntityId) {
        super(aEntityId);
    }

    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }

    @ScriptFunction(jsDoc = "Keys values used for identification of deleted data.")
    public ChangeValue[] getKeys() {
        return keys;
    }
}
