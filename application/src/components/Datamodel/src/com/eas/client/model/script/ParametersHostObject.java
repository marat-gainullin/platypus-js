/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.Model;
import com.eas.client.model.RowsetMissingException;
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
public class ParametersHostObject extends ScriptableObject {

    protected Parameters params;

    public ParametersHostObject(Parameters aParams, Scriptable aScope) throws RowsetMissingException {
        super(aScope, null);
        params = aParams;
    }

    @Override
    public String getClassName() {
        return "Parameters";
    }

    @Override
    public Object[] getIds() {
        try {
            Integer[] indexes = new Integer[params.getParametersCount()];
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
            return 0 <= aIndex && aIndex < params.getParametersCount();
        } catch (Exception ex) {
            Logger.getLogger(RowsetHostObject.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Object get(int aIndex, Scriptable start) {
        if (0 <= aIndex && aIndex < params.getParametersCount()) {
            try {
                Parameter param = params.get(aIndex + 1);
                if (param != null) {
                    Object value = param.getValue();
                    if (value instanceof CompactClob) {
                        value = ((CompactClob) value).getData();
                    }
                    return ScriptUtils.javaToJS(value, start);
                } else {
                    return Context.getUndefinedValue();
                }
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
        if (0 <= aIndex && aIndex < params.getParametersCount()) {
            try {
                Parameter param = params.get(aIndex + 1);
                if (param != null) {
                    param.setValue(ScriptUtils.js2Java(value));
                }
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
                        oGot = (Integer) params.getParametersCount();
                        break;
                    case Model.DATASOURCE_METADATA_SCRIPT_NAME:
                        oGot = FieldsHostObject.publishFields(params, this);
                        break;
                    default:
                        Parameter param = params.get(name);
                        if (param != null) {
                            Object value = param.getValue();
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
        Parameter param = params.get(name);
        if (param != null) {
            param.setValue(ScriptUtils.js2Java(value));
        } else {
            super.put(name, start, value);
        }
    }

    @Override
    public boolean has(String name, Scriptable start) {
        try {
            int colIndex = params.find(name);
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

    @Override
    public String toString() {
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (int l = 1; l <= params.getParametersCount(); l++) {
                if (l > 1) {
                    sb.append(", ");
                }
                sb.append(params.get(l).getName()).append(":").append(params.get(l).getValue());
            }
            sb.append("}");
            return sb.toString();
        } else {
            return super.toString();
        }
    }

    @Override
    public Object getDefaultValue(Class<?> aTypeHint) {
        if (params != null) {
            return toString();
        } else {
            return super.getDefaultValue(aTypeHint);
        }
    }

    public Parameters unwrap() {
        return params;
    }
}
