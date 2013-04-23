package com.bearsoft.rowset.changes;

public class Update extends Change {

    public Value[] keys;
    public Value[] data;

    public Update(String aEntityId)
    {
        super(aEntityId);
    }
    
    @Override
    public void accept(ChangeVisitor aChangeVisitor) throws Exception {
        aChangeVisitor.visit(this);
    }
}
