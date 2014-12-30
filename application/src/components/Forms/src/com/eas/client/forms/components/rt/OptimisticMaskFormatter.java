/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author mg
 */
public class OptimisticMaskFormatter extends MaskFormatter {

    protected DefaultFormatter nullMaskFormatter = new DefaultFormatter();

    public OptimisticMaskFormatter() {
        super();
        nullMaskFormatter.setOverwriteMode(false);
    }

    public OptimisticMaskFormatter(String aMask) throws ParseException {
        super(aMask);
        nullMaskFormatter.setOverwriteMode(false);
    }

    @Override
    public void install(JFormattedTextField ftf) {
        if (getMask() == null || getMask().isEmpty()) {
            nullMaskFormatter.install(ftf);
        } else {
            super.install(ftf);
        }
    }

    @Override
    public void uninstall() {
        if (getMask() == null || getMask().isEmpty()) {
            nullMaskFormatter.uninstall();
        } else {
            super.uninstall();
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (getMask() == null || getMask().isEmpty()) {
            return nullMaskFormatter.valueToString(value);//value != null ? value.toString() : null;
        } else {
            if (value instanceof String) {
                try {
                    return super.valueToString(value);
                } catch (ParseException ex) {
                    return super.valueToString(null);
                }
            } else {
                return super.valueToString(value);
            }
        }
    }

    @Override
    public Object stringToValue(String value) throws ParseException {
        if (getMask() == null || getMask().isEmpty()) {
            return nullMaskFormatter.stringToValue(value);//value;
        } else {
            try {
                return super.stringToValue(value);
            } catch (ParseException ex) {
                throw new ParseException(String.format("%s - is invalid text for %s mask.", value, getMask()), ex.getErrorOffset());
            }
        }
    }
}
