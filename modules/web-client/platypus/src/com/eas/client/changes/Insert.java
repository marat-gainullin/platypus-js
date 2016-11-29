package com.eas.client.changes;

import java.util.ArrayList;
import java.util.List;

public class Insert extends Change {

	private final List<Value> data = new ArrayList<>();

	public Insert(String aEntityName) {
		super(aEntityName);
	}

	public List<Value> getData() {
		return data;
	}

	@Override
	public void accept(ChangeVisitor aChangeVisitor) throws Exception {
		aChangeVisitor.visit(this);
	}
}
