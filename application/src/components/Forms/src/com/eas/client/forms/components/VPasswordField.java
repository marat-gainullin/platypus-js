/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components;

import com.eas.client.forms.api.components.HasValue;
import com.eas.script.ScriptFunction;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPasswordField;

/**
 *
 * @author Марат
 */
public class VPasswordField extends JPasswordField implements HasValue<String>{
    
    private String oldValue;

    public VPasswordField(String aText) {
        super.setText(aText != null ? aText : "");
        if (aText == null) {
            nullValue = true;
        }
        oldValue = aText;
        super.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                checkValueChanged();
            }

        });
        super.addActionListener((java.awt.event.ActionEvent e) -> {
            checkValueChanged();
        });
    }
    
    private void checkValueChanged() {
        String newValue = getValue();
        if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
            String wasOldValue = oldValue;
            oldValue = newValue;
            firePropertyChange(VALUE_PROP_NAME, wasOldValue, newValue);
        }
    }

    @Override
    public String getText() {
        return super.getPassword() != null ? new String(super.getPassword()) : "";
    }

    @ScriptFunction
    @Override
    public void setText(String aValue) {
        nullValue = false;
        super.setText(aValue != null ? aValue : "");
        checkValueChanged();
    }

    @Override
    public String getValue() {
        return nullValue ? null : super.getPassword() != null ? new String(super.getPassword()) : "";
    }

    private boolean nullValue;

    @ScriptFunction
    @Override
    public void setValue(String aValue) {
        nullValue = aValue == null;
        super.setText(aValue != null ? aValue : "");
        checkValueChanged();
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    private static final String VALUE_PROP_NAME = "value";   
}
