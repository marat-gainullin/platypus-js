/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;

/**
 *
 * @author mg
 */
public class VFormattedField extends JFormattedTextField implements HasValue<Object>, HasEmptyText, HasEditable {

    protected String format;
    protected int valueType;

    public VFormattedField(Object aValue) {
        super();
        setFocusLostBehavior(COMMIT);
        setValue(aValue);
    }

    public VFormattedField() {
        this(null);
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int aValue) {
        if (valueType != aValue) {
            valueType = aValue;
            setFormatterFactory(format != null ? FormatsUtils.formatterFactoryByFormat(format, valueType) : null);
        }
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String aValue) {
        if (format == null ? aValue != null : !format.equals(aValue)) {
            format = aValue;
            setFormatterFactory(format != null ? FormatsUtils.formatterFactoryByFormat(format, valueType) : null);
        }
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
            Logger.getLogger(VFormattedField.class.getName()).log(Level.WARNING, ex.getMessage());
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
