/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

/**
 *
 * @author mg
 */
public class Insert extends Change {

    public Value[] data;

    public Insert(String aEntityId)
    {
        super(aEntityId);
    }
    
    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }
}
