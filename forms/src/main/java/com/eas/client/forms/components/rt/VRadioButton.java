/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.beans.PropertyChangeListener;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Марат
 */
public class VRadioButton extends JRadioButton implements HasValue<Boolean> {

    private Boolean oldValue;

    public VRadioButton(String aText, boolean aSelected) {
        super(aText, aSelected);
        oldValue = aSelected;
        super.getModel().addChangeListener((ChangeEvent e) -> {
            checkValueChanged();
        });
    }

    private void checkValueChanged() {
        Boolean newValue = getValue();
        if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
            Boolean wasOldValue = oldValue;
            oldValue = newValue;
            firePropertyChange(VALUE_PROP_NAME, wasOldValue, newValue);
        }
    }

    @Override
    public Boolean getValue() {
        return super.isSelected();
    }

    @Override
    public void setValue(Boolean aValue) {
        super.setSelected(aValue != null ? aValue : false);
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    @Override
    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
    }

}
