package com.eas.client.changes;

public abstract class Change {

	private String entityName;

	public static class Value {

		private String name;
		private Object value;

		public Value(String aName, Object aValue) {
			name = aName;
			value = aValue;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
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
