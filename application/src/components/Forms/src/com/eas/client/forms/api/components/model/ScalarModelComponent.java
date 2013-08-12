/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.forms.FormRunner;
import com.eas.client.forms.api.Component;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.script.RowsetHostObject;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

/**
 *
 * @author mg
 */
public abstract class ScalarModelComponent<D extends DbControlPanel> extends Component<D> {

    protected boolean valid = true;

    public ScalarModelComponent() {
        super();
    }

    protected void validate() {
        try {
            if (!valid) {
                valid = true;
                delegate.configure();
                delegate.setEditingValue(delegate.getValueFromRowset());
                delegate.revalidate();
                delegate.repaint();
            }
        } catch (Exception ex) {
            Logger.getLogger(ScalarModelComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void invalidate() {
        if (valid) {
            valid = false;
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    validate();
                }
            });
        }
    }

    @ScriptFunction(jsDoc = "Model entity's field.")
    public Field getField() {
        if (delegate.getScriptScope() instanceof FormRunner) {
            return delegate.getDatamodelElement() != null ? delegate.getDatamodelElement().getField() : null;
        } else {
            return null;
        }
    }

    @ScriptFunction
    public void setField(Field aField) throws Exception {
        if (aField == null || (aField.getTag() instanceof Scriptable && aField.getTag() instanceof Wrapper)) {
            if (getField() != aField) {
                if (aField != null) {
                    checkModel(aField);
                }
                ModelElementRef modelRef = fieldToModelRef(aField);
                delegate.setDatamodelElement(modelRef);
                invalidate();
            }
        }
    }

    @ScriptFunction(jsDoc = "Component's on select event handler.")
    public Function getOnSelect() {
        return delegate.getOnSelect();
    }

    @ScriptFunction
    public void setOnSelect(Function aValue) throws Exception {
        delegate.setOnSelect(aValue);
    }

    @ScriptFunction(jsDoc = "Component's on render event handler.")
    public Function getOnRender() {
        return delegate.getOnRender();
    }

    @ScriptFunction
    public void setOnRender(Function aValue) throws Exception {
        delegate.setOnRender(aValue);
    }

    @ScriptFunction(jsDoc = "Component's value.")
    public Object getValue() throws Exception {
        validate();
        return delegate.getValue();
    }

    @ScriptFunction
    public void setValue(Object aValue) throws Exception {
        delegate.setValue(aValue);
    }
    protected JPanel errorPanel = new JPanel();

    @Override
    public void setError(String aValue) {
        super.setError(aValue);
        delegate.remove(errorPanel);
        if (aValue != null) {
            errorPanel.setBackground(Color.red);
            errorPanel.setToolTipText(aValue);
            errorPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 2));
            delegate.add(errorPanel, BorderLayout.SOUTH);
        }
        delegate.revalidate();
        delegate.repaint();
    }

    protected ModelElementRef fieldToModelRef(Field aField) throws Exception {
        if (aField != null) {
            RowsetHostObject<?> rowsetHost = ScriptRunnerPrototype.lookupEntity((Scriptable) aField.getTag());
            assert rowsetHost != null && rowsetHost.unwrap() instanceof ScriptableRowset;
            ApplicationEntity<?, ?, ?> entity = ((ScriptableRowset) rowsetHost.unwrap()).getEntity();
            if (entity != null) {
                return new ModelElementRef(aField, true, entity.getEntityId());
            }
        }
        return null;
    }

    protected void checkModel(Field aField) throws Exception {
        if (aField != null) {
            RowsetHostObject<?> rowsetHost = ScriptRunnerPrototype.lookupEntity((Scriptable) aField.getTag());
            assert rowsetHost != null && rowsetHost.unwrap() instanceof ScriptableRowset;
            ApplicationEntity<?, ?, ?> entity = ((ScriptableRowset) rowsetHost.unwrap()).getEntity();
            if (entity != null) {
                ApplicationModel<?, ?, ?, ?> model = entity.getModel();
                if (delegate.getModel() == null) {
                    delegate.setModel(model);
                }
            }
        }
    }
}
