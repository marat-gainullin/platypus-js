/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.forms.FormRunner;
import com.eas.client.model.ModelElementRef;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.script.ScriptFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

/**
 *
 * @author mg
 */
public class ModelCombo extends ScalarModelComponent<DbCombo> {

    protected ModelCombo(DbCombo aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelCombo() {
        super();
        setDelegate(new DbCombo());
    }

    @ScriptFunction(jsDoc = "Value field of the component.")
    public Field getValueField() {
        if (delegate.getScriptScope() instanceof FormRunner) {
            return delegate.getValueField() != null ? delegate.getValueField().getField() : null;
        } else {
            return null;
        }
    }

    @ScriptFunction
    public void setValueField(Field aField) throws Exception {
        if (aField == null || (aField.getTag() instanceof Scriptable && aField.getTag() instanceof Wrapper)) {
            if (getValueField() != aField) {
                if (aField != null) {
                    checkModel(aField);
                }
                ModelElementRef modelRef = fieldToModelRef(aField);
                delegate.setValueField(modelRef);
                invalidate();
            }
        }
    }

    @ScriptFunction(jsDoc = "Display field of the component.")
    public Field getDisplayField() {
        if (delegate.getScriptScope() instanceof FormRunner) {
            return delegate.getDisplayField() != null ? delegate.getDisplayField().getField() : null;
        } else {
            return null;
        }
    }

    @ScriptFunction
    public void setDisplayField(Field aField) throws Exception {
        if (aField == null || (aField.getTag() instanceof Scriptable && aField.getTag() instanceof Wrapper)) {
            if (getDisplayField() != aField) {
                if (aField != null) {
                    checkModel(aField);
                }
                ModelElementRef modelRef = fieldToModelRef(aField);
                delegate.setDisplayField(modelRef);
                invalidate();
            }
        }
    }

    @ScriptFunction(jsDoc = "Determines if component shown as list.")
    public boolean isList() throws Exception {
        return delegate.isList();
    }

    @ScriptFunction
    public void setList(boolean aValue) throws Exception {
        delegate.setList(aValue);
        invalidate();
    }

    @ScriptFunction(jsDoc = "Determines if component is editable.")
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
}
