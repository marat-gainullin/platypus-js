/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.forms.api.Component;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.dbcontrols.CellRenderEvent;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.script.EventMethod;
import com.eas.script.ScriptFunction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 * @param <D>
 */
public abstract class ScalarModelComponent<D extends DbControlPanel> extends Component<D> {

    public ScalarModelComponent() {
        super();
    }

    @Override
    protected void setDelegate(D aDelegate) {
        super.setDelegate(aDelegate);
        if (delegate != null) {
            try {
                delegate.configure();
            } catch (Exception ex) {
                Logger.getLogger(ScalarModelComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static final String FIELD_JSDOC = ""
            + "/**\n"
            + "* Model entity's field.\n"
            + "*/";

    @ScriptFunction(jsDoc = FIELD_JSDOC)
    public Field getField() {
        return delegate.getDatamodelElement() != null ? delegate.getDatamodelElement().getField() : null;
    }

    @ScriptFunction
    public void setField(Field aField) throws Exception {
        if (getField() != aField) {
            ModelElementRef modelRef = fieldToModelRef(aField);
            delegate.setDatamodelElement(modelRef);
            delegate.configure();
            delegate.setEditingValue(delegate.getValueFromRowset());
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String ON_SELECT_JSDOC = ""
            + "/**\n"
            + "* Component's selection event handler function.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_SELECT_JSDOC)
    public JSObject getOnSelect() {
        return delegate.getOnSelect();
    }

    @ScriptFunction
    public void setOnSelect(JSObject aValue) throws Exception {
        delegate.setOnSelect(aValue);
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String ON_RENDER_JSDOC = ""
            + "/**\n"
            + "* Component's rendering event handler function.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_RENDER_JSDOC)
    @EventMethod(eventClass = CellRenderEvent.class)
    public JSObject getOnRender() {
        return delegate.getOnRender();
    }

    @ScriptFunction
    public void setOnRender(JSObject aValue) throws Exception {
        delegate.setOnRender(aValue);
    }

    private static final String VALUE_JSDOC = ""
            + "/**\n"
            + "* Component's value.\n"
            + "*/";

    @ScriptFunction(jsDoc = VALUE_JSDOC)
    public Object getValue() throws Exception {
        return delegate.getValue();
    }

    @ScriptFunction
    public void setValue(Object aValue) throws Exception {
        if (aValue instanceof Number) {
            aValue = ((Number) aValue).doubleValue();
        }
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

    private static final String REDRAW_JSDOC = ""
            + "/**\n"
            + "* Redraw the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = REDRAW_JSDOC)
    public void redraw() {
        delegate.revalidate();
        delegate.repaint();
    }

    public static ApplicationEntity<?, ?, ?> resolveEntityByField(Field aField, ApplicationModel<?, ?, ?, ?> aModel) {
        ApplicationEntity<?, ?, ?> found = null;
        if (aField != null) {
            if (aModel.getParametersEntity().getFields().get(aField.getName()) == aField) {
                found = aModel.getParametersEntity();
            }
            if (found == null) {
                for (ApplicationEntity<?, ?, ?> entity : aModel.getEntities().values()) {
                    if (entity.getFields().get(aField.getName()) == aField) {
                        found = entity;
                        break;
                    }
                }
            }
        }
        return found;
    }

    protected ModelElementRef fieldToModelRef(Field aField) throws Exception {
        if (aField != null) {
            ApplicationEntity<?, ?, ?> found = resolveEntityByField(aField, delegate.getModel());
            return new ModelElementRef(aField, true, found != null ? found.getEntityId() : null);
        }
        return null;
    }

    private static final String MODEL_JSDOC = ""
            + "/**\n"
            + "* Model of the component. It will be used for data binding.\n"
            + "*/";

    @ScriptFunction(jsDoc = MODEL_JSDOC)
    public ApplicationModel<?, ?, ?, ?> getModel() {
        return delegate.getModel();
    }

    public void setModel(ApplicationModel<?, ?, ?, ?> aModel) throws Exception {
        delegate.setModel(aModel);
    }

}
