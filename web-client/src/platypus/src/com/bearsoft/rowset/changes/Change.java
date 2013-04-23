package com.bearsoft.rowset.changes;

import com.bearsoft.rowset.metadata.DataTypeInfo;

public abstract class Change {
	
    public String entityId;

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

    public Change(String aEntityId)
    {
        super();
        entityId = aEntityId;
    }
    
    public abstract void accept(ChangeVisitor aChangeVisitor) throws Exception;
}
