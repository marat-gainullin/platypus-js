package com.eas.client.changes;

import java.util.ArrayList;
import java.util.List;

public class Update extends Change {

	private final List<Value> keys = new ArrayList<>();
	private final List<Value> data = new ArrayList<>();

	public Update(String aEntityName) {
		super(aEntityName);
	}

	public List<Value> getKeys() {
		return keys;
	}

	public List<Value> getData() {
		return data;
	}

	@Override
	public void accept(ChangeVisitor aChangeVisitor) throws Exception {
		aChangeVisitor.visit(this);
	}
}
