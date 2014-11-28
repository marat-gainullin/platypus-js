/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components;

import java.beans.PropertyChangeListener;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author Марат
 */
public class VSlider extends JSlider {

    private int oldValue;

    public VSlider(int aOrientation, int min, int max, int value) {
        super(aOrientation, min, max, value);
        oldValue = value;
        super.addChangeListener((ChangeEvent e) -> {
            checkValueChanged();
        });
    }

    private void checkValueChanged() {
        int newValue = getValue();
        if (oldValue != newValue) {
            int wasOldValue = oldValue;
            oldValue = newValue;
            firePropertyChange(VALUE_PROP_NAME, wasOldValue, newValue);
        }
    }

    @Override
    public int getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(int aValue) {
        super.setValue(aValue);
        checkValueChanged();
    }

    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    private static final String VALUE_PROP_NAME = "value";
}
