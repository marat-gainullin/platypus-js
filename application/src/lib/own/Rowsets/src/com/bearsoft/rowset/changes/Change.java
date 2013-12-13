/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.changes;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public abstract class Change {

    public String entityId;
    public boolean consumed;

    public static class Value {

        public String name;
        public Object value;
        public DataTypeInfo type;

        public Value(String aName, Object aValue, DataTypeInfo aType) {
            name = aName;
            value = aValue;
            type = aType;
        }

        @ScriptFunction(jsDoc = "Name of changed property.")
        public String getName() {
            return name;
        }

        @ScriptFunction(jsDoc = "New value.")
        public Object getValue() {
            return value;
        }
    }

    public Change(String aEntityId) {
        super();
        entityId = aEntityId;
    }

    @ScriptFunction(jsDoc = "Consumes the change, so other validators and database applier won't apply it.")
    public void consume() {
        consumed = true;
    }

    @ScriptFunction(jsDoc = "Indicated if the change is consumed.")
    public boolean isConsumed() {
        return consumed;
    }

    public abstract void accept(ChangeVisitor aChangeVisitor) throws Exception;

    @ScriptFunction(jsDoc = "Indicates the change's type (Insert, Update, Delete or Command).")
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getType();
    }
}
