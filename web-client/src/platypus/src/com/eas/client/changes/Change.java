package com.eas.client.changes;

import com.eas.client.metadata.DataTypeInfo;

public abstract class Change {

	private String entityName;

	public static class Value {

		public String name;
		public Object value;
		public DataTypeInfo type;

		public Value(String aName, Object aValue, DataTypeInfo aType) {
			name = aName;
			value = aValue;
			type = aType;
		}
	}

	public Change(String aEntityName) {
		super();
		entityName = aEntityName;
	}

	public String getEntityName() {
		return entityName;
	}

	public abstract void accept(ChangeVisitor aChangeVisitor) throws Exception;
}
