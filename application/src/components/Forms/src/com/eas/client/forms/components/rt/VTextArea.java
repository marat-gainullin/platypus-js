/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextPane;

/**
 *
 * @author Марат
 */
public class VTextArea extends JTextPane implements HasValue<String>, HasEditable, HasEmptyText {

    private String oldValue;

    public VTextArea(String aText) {
        super();
        super.setText(aText != null ? aText : "");
        if (aText == null) {
            nullValue = true;
        }
        oldValue = aText;
        super.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                String text = getText();
                if (text != null && !"".equals(text)) {
                    nullValue = false;
                }
                checkValueChanged();
            }

        });
    }

    public VTextArea() {
        this(null);
    }

    protected void checkValueChanged() {
        String newValue = getValue();
        if (oldValue == null ? newValue != null : !oldValue.equals(newValue)) {
            String wasOldValue = oldValue;
            oldValue = newValue;
            firePropertyChange(VALUE_PROP_NAME, wasOldValue, newValue);
        }
    }

    @Override
    public String getValue() {
        return nullValue ? null : super.getText();
    }

    private boolean nullValue;

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

    @Override
    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    @Override
    public boolean getEditable() {
        return super.isEditable();
    }

    @Override
    public String getText() {
        return super.getText();
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
    public void setText(String aValue) {
        nullValue = false;
        super.setText(aValue != null ? aValue : "");
        checkValueChanged();
    }
}
