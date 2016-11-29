/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VTextArea;
import com.eas.design.Undesignable;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JLabel;
import javax.swing.JTable;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 *
 * if(aValue instanceof Number){ Number n = (Number)aValue; DecimalFormat df =
 * new DecimalFormat("#.#"); aValue = df.format(n.doubleValue()); }
 *
 */
public class ModelTextArea extends ModelComponentDecorator<VTextArea, String> implements HasPublished, HasEmptyText, HasEditable {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model components for a text area.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelTextArea() {
        super();
        setDecorated(new VTextArea());
        setBackground(getDecorated().getBackground());
    }

    @ScriptFunction(name = "value", jsDoc = JS_VALUE_JSDOC)
    @Undesignable
    @Override
    public Object getJsValue() {
        return super.getJsValue();
    }

    @ScriptFunction
    @Override
    public void setJsValue(Object aValue) {
        setValue(aValue != null ? JSType.toString(aValue) : null);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    @Override
    public boolean getEditable() {
        return decorated.isEditable();
    }

    @ScriptFunction
    @Override
    public void setEditable(boolean aValue) {
        decorated.setEditable(aValue);
    }

    @ScriptFunction
    @Override
    public String getEmptyText() {
        return decorated.getEmptyText();
    }

    @ScriptFunction
    @Override
    public void setEmptyText(String aValue) {
        decorated.setEmptyText(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        decorated.setText(aValue);
    }

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        removeAll();
        JLabel rendererLine = new JLabel(decorated.getText());
        rendererLine.setOpaque(false);
        add(rendererLine, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        EventQueue.invokeLater(() -> {
            decorated.requestFocus();
        });
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
