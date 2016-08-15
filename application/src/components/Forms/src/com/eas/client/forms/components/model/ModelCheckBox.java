/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.components.rt.VCheckBox;
import com.eas.design.Undesignable;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ModelCheckBox extends ModelComponentDecorator<VCheckBox, Boolean> implements HasPublished {

    public ModelCheckBox() {
        super();
        setDecorated(new VCheckBox());
        setOpaque(false);
        setBorder(null);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * An implementation of a model check box -- an item that can be selected or deselected, and which displays its state to the user.\n"
            + " * @param text the text of the component (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public ModelCheckBox(String aText) throws Exception {
        this();
        decorated.setText(aText);
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

    @ScriptFunction(name = "value", jsDoc = JS_VALUE_JSDOC)
    @Undesignable
    @Override
    public Object getJsValue() {
        return super.getJsValue();
    }

    @ScriptFunction
    @Override
    public void setJsValue(Object aValue) {
        if (aValue instanceof Boolean) {
            setValue((Boolean) aValue);
        } else if (aValue != null) {
            setValue(JSType.toBoolean(aValue));
        } else {
            setValue(null);
        }
    }

    @Override
    public void setDecorated(VCheckBox aComponent) {
        super.setDecorated(aComponent);
        if (decorated != null) {
            decorated.setOpaque(false);
        }
    }

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text on the check box."
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        decorated.setText(aValue);
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        return event instanceof MouseEvent
                || (event instanceof KeyEvent && ((KeyEvent) event).getKeyCode() == KeyEvent.VK_F2)
                || (event instanceof ActionEvent);
    }

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        decorated.setHorizontalAlignment(SwingConstants.CENTER);
        decorated.setHorizontalTextPosition(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        decorated.setHorizontalAlignment(SwingConstants.CENTER);
        decorated.setHorizontalTextPosition(SwingConstants.CENTER);
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
