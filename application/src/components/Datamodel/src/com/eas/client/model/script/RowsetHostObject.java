/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.Model;
import com.eas.client.model.RowsetMissingException;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.script.NativeJavaHostObject;
import com.eas.script.ScriptUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class RowsetHostObject<E extends ApplicationEntity<?, ?, E>> extends NativeJavaHostObject {

    protected ScriptableRowset<E> rowset;

    public RowsetHostObject(ScriptableRowset<E> aRowset, Scriptable aScope) throws Exception {
        super(aScope, aRowset, null);
        rowset = aRowset;
        rowset.setTag(this);
        defineProperties();
    }

    /**
     * Defines properties in entity's scope.
     *
     * @param aScope Scope to install new scriptables to.
     * @throws RowsetMissingException
     */
    public void defineProperties() throws Exception {
        E entity = rowset.getEntity();
        try {
            defineFieldsProperties();
            defineProperty(Model.DATASOURCE_METADATA_SCRIPT_NAME, FieldsHostObject.publishFields(rowset.getFields(), this));
            defineProperty("md", FieldsHostObject.publishFields(rowset.getFields(), this));
        } catch (Exception ex) {
            Logger.getLogger(RowsetHostObject.class.getName()).log(Level.WARNING, ex.getMessage());
        }
        if (entity != null && entity.getQuery() != null) {
            defineProperty(Model.PARAMETERS_SCRIPT_NAME, new ParametersHostObject(entity.getQuery().getParameters(), this));
        }
    }

    // Fields values access interface filling
    private void defineFieldsProperties() throws RowsetMissingException {
        Fields md = rowset.getFields();
        rowset.createScriptableFields();
        if (md != null) {
            for (int i = 1; i <= md.getFieldsCount(); i++) {
                Field field = md.get(i);
                String fName = field.getName();
                if (fName != null && !fName.isEmpty()) {
                    defineProperty(fName, rowset.getScriptableField(fName), ScriptableRowset.getValueScriptableFieldMethod, ScriptableRowset.setValueScriptableFieldMethod);
                }
            }
        }
    }

    @Override
    public Object get(String name, Scriptable start) {
        return super.get(name, start);
    }

    @Override
    public Object[] getIds() {
        try {
            Integer[] indexes = new Integer[rowset.getLength()];
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
            return 0 <= aIndex && aIndex < rowset.getSize();
        } catch (Exception ex) {
            Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Object get(int aIndex, Scriptable start) {
        try {
            if (0 <= aIndex && aIndex < rowset.getSize()) {
                try {
                    return rowset.getRow(aIndex + 1);
                } catch (Exception ex) {
                    Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
                    return NOT_FOUND;
                }
            } else {
                return NOT_FOUND;
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        try {
            if (0 <= index && index < rowset.getSize()) {
                if (value != Context.getUndefinedValue() && value != null) {
                    assert value instanceof ScriptableObject;
                    ScriptableObject sValue = (ScriptableObject) value;
                    Row row = rowset.getRowset().getRow(index + 1);
                    for (Object jsId : sValue.getIds()) {
                        if (jsId instanceof String) {
                            int colIndex = rowset.getRowset().getFields().find((String) jsId);
                            if (colIndex > 0) {
                                row.setColumnObject(
                                        colIndex,
                                        ScriptUtils.js2Java(sValue.get(jsId)));
                            }
                        }
                    }
                    /*
                     * } else { Row row = rowset.getRowset().getRow(index + 1);
                     * for (int i = 1; i <= row.getFields().getFieldsCount();
                     * i++) { row.setColumnObject(i, null); } }
                     */
                } else {
                    throw new IllegalArgumentException("Assigned value must be non-null and not undefined");
                }
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
