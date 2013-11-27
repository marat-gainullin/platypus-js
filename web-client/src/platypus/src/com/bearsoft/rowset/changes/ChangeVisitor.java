package com.bearsoft.rowset.changes;

public interface ChangeVisitor {

	public void visit(Insert aChange) throws Exception;

	public void visit(Update aChange) throws Exception;

	public void visit(Delete aChange) throws Exception;

	public void visit(Command aChange) throws Exception;
}
