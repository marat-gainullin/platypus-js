/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.eas.client.model.Model;
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class RowHostObject extends ScriptableObject {

    protected Row row = null;

    public RowHostObject(Scriptable aScope, Row aRow) {
        super(aScope, null);
        row = aRow;
        defineFunctionProperties(new String[]{"unwrap", "getColumnObject", "getLength", "toString"}, RowHostObject.class, EMPTY);
    }

    @Override
    public Object[] getIds() {
        try {
            Integer[] indexes = new Integer[row.getColumnCount()];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }
            return indexes;
        } catch (Exception ex) {
            Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
            return super.getIds();
        }
    }

    @Override
    public boolean has(int aIndex, Scriptable aStart) {
        try {
            return 0 <= aIndex && aIndex < row.getColumnCount();
        } catch (Exception ex) {
            Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Object get(int aIndex, Scriptable start) {
        if (0 <= aIndex && aIndex < row.getColumnCount()) {
            try {
                Object value = row.getColumnObject(aIndex + 1);
                if (value instanceof CompactClob) {
                    value = ((CompactClob) value).getData();
                }
                return ScriptUtils.javaToJS(value, start);
            } catch (Exception ex) {
                Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
                return NOT_FOUND;
            }
        } else {
            return NOT_FOUND;
        }
    }

    @Override
    public void put(int aIndex, Scriptable start, Object value) {
        if (0 <= aIndex && aIndex < row.getColumnCount()) {
            try {
                row.setColumnObject(aIndex + 1, ScriptUtils.js2Java(value));
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object oGot = super.get(name, start);
        try {
            if (oGot == Scriptable.NOT_FOUND) {
                switch (name) {
                    case "length":
                        oGot = (Integer) row.getColumnCount();
                        break;
                    case Model.DATASOURCE_METADATA_SCRIPT_NAME:
                        oGot = FieldsHostObject.publishFields(row.getFields(), this);
                        break;
                    default:
                        int colIndex = row.getFields().find(name);
                        if (colIndex > 0) {
                            Object value = row.getColumnObject(colIndex);
                            if (value instanceof CompactClob) {
                                value = ((CompactClob) value).getData();
                            }
                            return ScriptUtils.javaToJS(value, start);
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, null, ex);
        }
        return oGot;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        try {
            int colIndex = row.getFields().find(name);
            if (colIndex > 0) {
                row.setColumnObject(colIndex, ScriptUtils.js2Java(value));
            } else {
                super.put(name, start, value);
            }
        } catch (EvaluatorException | RowsetException ex) {
            Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean has(String name, Scriptable start) {
        try {
            int colIndex = row.getFields().find(name);
            if (colIndex > 0) {
                return true;
            } else {
                return super.has(name, start);
            }
        } catch (Exception ex) {
            Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.has(name, start);
    }

    public Row unwrap() {
        return row;
    }

    public Object getColumnObject(int aColIndex) throws InvalidColIndexException {
        return row.getColumnObject(aColIndex);
    }

    public int getLength() {
        return row.getColumnCount();
    }

    @Override
    public String toString() {
        if (row != null) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                for (int l = 1; l <= row.getColumnCount(); l++) {
                    if (l > 1) {
                        sb.append(", ");
                    }
                    sb.append(row.getFields().get(l).getName()).append(":").append(row.getColumnObject(l));
                }
                sb.append("}");
                return sb.toString();
            } catch (InvalidColIndexException ex) {
                Logger.getLogger(Row.class.getName()).log(Level.SEVERE, null, ex);
                return super.toString();
            }
        } else {
            return super.toString();
        }
    }

    @Override
    public String getClassName() {
        return Row.class.getSimpleName();
    }

    @Override
    public Object getDefaultValue(Class<?> aTypeHint) {
        if (row != null) {
            return toString();
        } else {
            return super.getDefaultValue(aTypeHint);
        }
    }
    
    public static RowHostObject publishRow(Scriptable aScope, Row aRow) throws Exception {
        Object published = aRow.getTag();
        if (published == null) {
            published = new RowHostObject(aScope, aRow);
            aRow.setTag(published);
        }
        assert published instanceof RowHostObject;
        return (RowHostObject) published;
    }
}
