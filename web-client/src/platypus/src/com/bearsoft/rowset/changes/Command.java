package com.bearsoft.rowset.changes;

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
}
