/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components;

import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;

/**
 *
 * @author Марат
 */
public class VFormattedField extends JFormattedTextField implements HasValue<Object>, HasEmptyText, HasEditable {

    public VFormattedField(Object aValue) {
        super();
        setValue(aValue);
    }

    public VFormattedField() {
        this(null);
    }

    @Override
    public String getText() {
        return super.getText() != null ? super.getText() : "";
    }

    @Override
    public void setText(String aValue) {
        try {
            super.setText(aValue != null ? aValue : "");
            super.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(VFormattedField.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Object aValue) {
        if (aValue instanceof Number) {
            aValue = ((Number) aValue).doubleValue();
        }
        if (super.getFormatterFactory() == null && aValue instanceof String) {
            super.setFormatterFactory(FormatsUtils.formatterFactoryByFormat(getFormat(), FormatsUtils.MASK));
        }
        super.setValue(aValue);
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    private static final String VALUE_PROP_NAME = "value";

    public String getFormat() {
        if (super.getFormatter() != null) {
            return FormatsUtils.formatByFormatter(super.getFormatter());
        } else {
            if (super.getFormatterFactory() != null) {
                return FormatsUtils.formatByFormatter(super.getFormatterFactory().getFormatter(this));
            } else {
                return null;
            }
        }
    }

    public void setFormat(String aValue) throws ParseException {
        if (getFormat() == null ? aValue != null : !getFormat().equals(aValue)) {
            if (super.getFormatter() != null) {
                FormatsUtils.applyFormat(super.getFormatter(), aValue);
                super.setText(super.getFormatter().valueToString(super.getValue()));
            }
        }
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
}
