package com.bearsoft.rowset.changes;

public class Delete extends Change {

    public Value[] keys;

    public Delete(String aEntityId)
    {
        super(aEntityId);
    }
    
    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }
}
