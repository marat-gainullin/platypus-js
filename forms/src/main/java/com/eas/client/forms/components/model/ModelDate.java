/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VDateTimeField;
import com.eas.design.Undesignable;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ModelDate extends ModelComponentDecorator<VDateTimeField, Date> implements HasPublished, HasEmptyText, HasEditable {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a date. \n"
            + " */";
    private boolean isDateView = true;
    private boolean isTimeView = true;
    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelDate() {
        super();
        setDecorated(new VDateTimeField());
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
        if (aValue == null || "".equals(aValue)) {
            setValue(null);
        } else if (aValue instanceof Date) {
            setValue((Date) aValue);
        } else {
            setValue(new Date(JSType.toLong(aValue)));
        }
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
        return decorated.getEditable();
    }

    @ScriptFunction
    @Override
    public void setEditable(boolean aValue) {
        decorated.setEditable(aValue);
    }

    @ScriptFunction
    public String getFormat() {
        return decorated.getDateFormat();
    }

    @ScriptFunction
    public void setFormat(String aValue) throws Exception {
        decorated.setDateFormat(aValue);
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

    @ScriptFunction
    public boolean getDatePicker() {
        return isDateView;
    }

    @ScriptFunction
    public void setDatePicker(boolean aValue) {
        this.isDateView = aValue;
    }
    
    @ScriptFunction
    public boolean getTimePicker() {
        return isTimeView;
    }

    @ScriptFunction
    public void setTimePicker(boolean aValue) {
        this.isTimeView = aValue;
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
        JFormattedTextField ftf = decorated.getEditorComponent();
        ftf.getActionMap().remove(TextFieldsCommitAction.COMMIT_ACTION_NAME);
        ftf.getActionMap().put(TextFieldsCommitAction.COMMIT_ACTION_NAME, new TextFieldsCommitAction(ftf));
        EventQueue.invokeLater(() -> {
            decorated.requestFocus();
        });
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean stopCellEditing() {
        JFormattedTextField ftf = decorated.getEditorComponent();
        try {
            ftf.commitEdit();
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
