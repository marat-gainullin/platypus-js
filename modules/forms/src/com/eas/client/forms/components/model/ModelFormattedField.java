/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VFormattedField;
import com.eas.design.Undesignable;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ModelFormattedField extends ModelComponentDecorator<VFormattedField, Object> implements HasPublished, HasEmptyText, HasEditable {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a date.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelFormattedField() {
        super();
        setDecorated(new VFormattedField() {

            @Override
            protected JSObject getPublished() {
                return ModelFormattedField.this.getPublished();
            }
        });
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

    @ScriptFunction
    @Undesignable
    public JSObject getOnFormat() {
        return decorated.getOnFormat();
    }

    @ScriptFunction
    public void setOnFormat(JSObject aValue) {
        decorated.setOnFormat(aValue);
    }

    @ScriptFunction
    @Undesignable
    public JSObject getOnParse() {
        return decorated.getOnParse();
    }

    @ScriptFunction
    public void setOnParse(JSObject aValue) {
        decorated.setOnParse(aValue);
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

    private static final String FORMAT_JSDOC = ""
            + "/**\n"
            + "* The format string of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = FORMAT_JSDOC)
    public String getFormat() {
        return decorated.getFormat();
    }

    @ScriptFunction
    public void setFormat(String aValue) throws ParseException {
        decorated.setFormat(aValue);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * ValueType hint for the field. It is used to determine, how to interpret format pattern.\n"
            + " */")
    public int getValueType() {
        return decorated.getValueType();
    }

    @ScriptFunction
    public void setValueType(int aValue) {
        decorated.setValueType(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        decorated.setText(aValue);
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

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        removeAll();
        JLabel rendererLine = new JLabel(decorated.getText());
        rendererLine.setOpaque(false);
        add(rendererLine, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (decorated != null) {
            decorated.getActionMap().remove(TextFieldsCommitAction.COMMIT_ACTION_NAME);
            decorated.getActionMap().put(TextFieldsCommitAction.COMMIT_ACTION_NAME, new TextFieldsCommitAction(decorated));
        }
        EventQueue.invokeLater(() -> {
            decorated.requestFocus();
        });
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean stopCellEditing() {
        try {
            decorated.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(ModelDate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.stopCellEditing();
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
