/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JEditorPane;

/**
 *
 * @author Марат
 */
public class VHtmlArea extends JEditorPane implements HasValue<String>, HasEmptyText, HasEditable {

    private String oldValue;

    public VHtmlArea(String aText) {
        super();
        super.setContentType("text/html");
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
    public boolean getEditable() {
        return super.isEditable();
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
