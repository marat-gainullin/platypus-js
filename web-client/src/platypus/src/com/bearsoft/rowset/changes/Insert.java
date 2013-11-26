package com.bearsoft.rowset.changes;

public class Insert extends Change {

	public Value[] data;

	public Insert(String aEntityId) {
		super(aEntityId);
	}

	@Override
	public void accept(ChangeVisitor aChangeVisitor) throws Exception {
		aChangeVisitor.visit(this);
	}
}
