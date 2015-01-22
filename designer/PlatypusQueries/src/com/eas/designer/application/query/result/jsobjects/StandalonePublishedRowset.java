/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.result.jsobjects;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.eas.script.NoPublisherException;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class StandalonePublishedRowset extends AbstractJSObject {

    protected static class StandalonePublishedRow extends AbstractJSObject {

        protected Row row;

        public StandalonePublishedRow(Row aRow) {
            super();
            row = aRow;
        }

        @Override
        public boolean hasMember(String name) {
            return row.getFields().find(name) != -1;
        }

        @Override
        public Object getMember(String name) {
            if (hasMember(name)) {
                try {
                    return row.getColumnObject(row.getFields().find(name));
                } catch (InvalidColIndexException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            return null;
        }

        @Override
        public void setMember(String name, Object value) {
            if (hasMember(name)) {
                try {
                    row.setColumnObject(row.getFields().find(name), value);
                } catch (RowsetException ex) {
                    Exceptions.printStackTrace(ex);
                }
            } else {
                super.setMember(name, value);
            }
        }

        private Row getRow() {
            return row;
        }
    }

    protected Rowset rowset;
    protected String entityName;
    protected JSObject jsSplice = new AbstractJSObject() {

        @Override
        public Object call(Object thiz, Object... args) {
            try {
                int insertAt = JSType.toInteger(args[0]);
                int howManyToDelete = JSType.toInteger(args[1]);
                for (int i = 0; i < howManyToDelete; i++) {
                    rowset.deleteAt(insertAt + 1);
                }
                for (int i = 2; i < args.length; i++) {
                    StandalonePublishedRow rowWrapper = (StandalonePublishedRow) args[i];
                    if (args.length > 2) {
                        rowset.insertAt(rowWrapper.getRow(), false, (insertAt + 1) + (i - 2), new Object[]{});
                    }
                }
            } catch (RowsetException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }

    };
    protected JSObject jsIndexOf = new AbstractJSObject() {

        @Override
        public Object call(Object thiz, Object... args) {
            if (args.length > 0 && args[0] instanceof StandalonePublishedRow) {
                StandalonePublishedRow target = (StandalonePublishedRow) args[0];
                Row row = target.getRow();
                return rowset.getCurrent().indexOf(row);
            } else {
                return -1;
            }
        }

    };
    protected JSObject jsElementClass = new AbstractJSObject() {

        @Override
        public boolean isFunction() {
            return true;
        }

        @Override
        public Object newObject(Object... args) {
            Row row = new Row("", rowset.getFields());
            row.setLog(rowset.getLog());
            StandalonePublishedRow rowWrapper = new StandalonePublishedRow(row);
            row.setPublished(rowWrapper);
            return rowWrapper;
        }

    };
    protected JSObject jsCursor;

    public StandalonePublishedRowset(Rowset aRowset) {
        super();
        rowset = aRowset;
        jsCursor = (JSObject) getSlot(0);
    }

    @Override
    public boolean hasMember(String name) {
        return "length".equals(name)
                || "splice".equals(name)
                || "cursor".equals(name)
                || "indexOf".equals(name)
                || "elementClass".equals(name);
    }

    @Override
    public void setMember(String name, Object value) {
        if ("cursor".equals(name) && value instanceof JSObject) {
            jsCursor = (JSObject) value;
        } else {
            super.setMember(name, value);
        }
    }

    @Override
    public Object getMember(String name) {
        if (name != null) {
            switch (name) {
                case "length":
                    return rowset.size();
                case "splice":
                    return jsSplice;
                case "indexOf":
                    return jsIndexOf;
                case "elementClass":
                    return jsElementClass;
                case "cursor":
                    return jsCursor;
            }
        }
        return super.getMember(name);
    }

    @Override
    public boolean hasSlot(int slot) {
        return slot >= 0 && slot < rowset.size();
    }

    @Override
    public Object getSlot(int index) {
        if (hasSlot(index)) {
            Row row = rowset.getCurrent().get(index);
            try {
                if (row.getPublished() == null) {
                    row.setPublished(new StandalonePublishedRow(row));
                }
            } catch (NoPublisherException ex) {
                row.setPublished(new StandalonePublishedRow(row));
            }
            return row.getPublished();
        } else {
            return super.getSlot(index);
        }
    }

}
