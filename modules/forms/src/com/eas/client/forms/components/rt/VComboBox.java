/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mg
 */
public class VComboBox<T> extends JComboBox<T> implements HasValue<T>, HasEmptyText, HasEditable {

    protected Object oldValue;

    public VComboBox() {
        super();
        super.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                Object newValue = e.getItem();
                if (oldValue != newValue) {
                    Object wasOldValue = oldValue;
                    oldValue = newValue;
                    firePropertyChange(VALUE_PROP_NAME, wasOldValue, newValue);
                }
            }
        });
    }

    @Override
    public T getValue() {
        return (T)getModel().getSelectedItem();
    }

    @Override
    public void setValue(T aValue) {
        T wasValue = getValue();
        if (wasValue != aValue) {
            getModel().setSelectedItem(aValue);
            if (aValue == null) {
                oldValue = null;
                firePropertyChange(VALUE_PROP_NAME, wasValue, null);
            }
        }
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    @Override
    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    protected String emptyText;

    @Override
    public String getEmptyText() {
        return emptyText;
    }

    @Override
    public void setEmptyText(String aValue) {
        emptyText = aValue;
    }

    @Override
    public boolean getEditable() {
        return super.isEditable();
    }

    public String getText() {
        return ((JTextComponent) super.getEditor().getEditorComponent()).getText();
    }

    public void setText(String aValue) {
        if (isEditable()) {
            ((JTextComponent) super.getEditor().getEditorComponent()).setText(aValue);
        }
    }
}
