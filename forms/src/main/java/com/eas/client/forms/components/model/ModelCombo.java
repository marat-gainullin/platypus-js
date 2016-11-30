/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model;

import com.eas.client.forms.Forms;
import com.eas.client.forms.components.rt.HasEditable;
import com.eas.client.forms.components.rt.HasEmptyText;
import com.eas.client.forms.components.rt.VComboBox;
import com.eas.design.Designable;
import com.eas.design.Undesignable;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class ModelCombo extends ModelComponentDecorator<VComboBox<JSObject>, Object> implements HasPublished, HasEmptyText, HasEditable {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that combines a button or editable field and a drop-down list.\n"
            + " */";

    protected Object injected;
    protected JSObject displayList;
    protected JSObject boundToList;
    protected String displayField;

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelCombo() {
        super();
        setDecorated(new VComboBox());
        decorated.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                setText(renderValue(value));
                return this;
            }
        });
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
        Object oldValue = getJsValue();
        if (oldValue != aValue) {
            DefaultComboBoxModel<JSObject> dm = ((DefaultComboBoxModel<JSObject>) decorated.getModel());
            int newValueIndex = dm.getIndexOf(aValue);
            if (injected != null) {
                int injectedValueIndex = dm.getIndexOf(injected);
                if (injectedValueIndex != -1) {
                    dm.removeElementAt(injectedValueIndex);
                }
            }
            injected = null;
            if (aValue instanceof JSObject && newValueIndex == -1) {
                dm.addElement((JSObject) aValue);
                injected = aValue;
            }
            setValue(aValue);
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

    protected boolean listChangedEnqueued;

    protected void enqueueListChanged() {
        listChangedEnqueued = true;
        EventQueue.invokeLater(() -> {
            if (listChangedEnqueued) {
                listChangedEnqueued = false;
                refill();
            }
        });
    }

    protected void unbindList() {
        if (boundToList != null) {
            Scripts.unlisten(boundToList);
            boundToList = null;
        }
    }

    protected void bindList() {
        if (displayList != null && Scripts.isInitialized()) {
            boundToList = Scripts.getSpace().listen(displayList, "length", new AbstractJSObject() {

                @Override
                public Object call(Object thiz, Object... args) {
                    enqueueListChanged();
                    return null;
                }

            });
        }
    }

    private static final String DISPLAY_LIST_JSDOC = ""
            + "/**\n"
            + "* List of displayed options in a dropdown list of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = DISPLAY_LIST_JSDOC)
    @Designable(category = "model")
    public JSObject getDisplayList() {
        return displayList;
    }

    @ScriptFunction
    public void setDisplayList(JSObject aValue) throws Exception {
        if (displayList != aValue) {
            unbindList();
            displayList = aValue;
            bindList();
            refill();
            decorated.revalidate();
            decorated.repaint();
        }
    }

    protected void refill() {
        JSObject value = (JSObject) getJsValue();
        DefaultComboBoxModel<JSObject> dm = ((DefaultComboBoxModel<JSObject>) decorated.getModel());
        dm.removeAllElements();
        boolean valueMet = false;
        if (displayList != null && list) {
            int length = JSType.toInteger(displayList.getMember("length"));
            for (int i = 0; i < length; i++) {
                Object oItem = displayList.getSlot(i);
                if (oItem instanceof JSObject) {
                    if (oItem.equals(value)) {
                        valueMet = true;
                    }
                    dm.addElement((JSObject) oItem);
                }
            }
        }
        if (value != null) {
            if (!valueMet) {
                dm.addElement(value);
                injected = value;
            }
            dm.setSelectedItem(value);
        }
    }

    private static final String DISPLAY_FIELD_JSDOC = ""
            + "/**\n"
            + "* Display field of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = DISPLAY_FIELD_JSDOC)
    @Designable(category = "model")
    public String getDisplayField() {
        return displayField;
    }

    @ScriptFunction
    public void setDisplayField(String aField) throws Exception {
        if (displayField == null ? aField != null : !displayField.equals(aField)) {
            displayField = aField;
            decorated.revalidate();
            decorated.repaint();
        }
    }

    private static final String LIST_JSDOC = ""
            + "/**\n"
            + "* Determines if component shown as a list.\n"
            + "*/";

    protected boolean list = true;

    @ScriptFunction(jsDoc = LIST_JSDOC)
    public boolean getList() throws Exception {
        return list;
    }

    @ScriptFunction
    public void setList(boolean aValue) throws Exception {
        if (list != aValue) {
            list = aValue;
            refill();
            decorated.revalidate();
            decorated.repaint();
        }
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

    private String renderValue(Object aValue) {
        return aValue instanceof JSObject ? JSType.toString(ModelWidget.getPathData((JSObject) aValue, displayField)) : "";
    }

    @Override
    protected void setupCellRenderer(JTable table, int row, int column, boolean isSelected) {
        removeAll();
        String rendered = renderValue(getValue());
        JLabel rendererLine = new JLabel(rendered);
        rendererLine.setOpaque(false);
        add(rendererLine, BorderLayout.CENTER);
    }

    @Override
    public boolean isFieldContentModified() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
