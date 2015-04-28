package com.eas.client.changes;

import java.util.ArrayList;
import java.util.List;

public class Delete extends Change {

	private final List<Value> keys = new ArrayList<>();

	public Delete(String aEntityName) {
		super(aEntityName);
	}

	public List<Value> getKeys() {
		return keys;
	}

	@Override
	public void accept(ChangeVisitor aChangeVisitor) throws Exception {
		aChangeVisitor.visit(this);
	}
}
