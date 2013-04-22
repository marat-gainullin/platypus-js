/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.date.rt;

import de.wannawork.jcalendar.JCalendarComboBox;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DateFormatter;
import javax.swing.text.MaskFormatter;

/**
 * This class is intended to convert values to string and vice versa without special date control.
 * More likely to use it in renderers.
 * @author mg
 */
public class RendererOptimisticDateFormatter extends DateFormatter {

    protected MaskFormatter mf = null;

    public RendererOptimisticDateFormatter(SimpleDateFormat lFormat) {
        super(lFormat);
        try {
            mf = new MaskFormatter(lFormat.toPattern().replaceAll("[mMhHsYyda]", " "));
        } catch (ParseException ex) {
            Logger.getLogger(JCalendarComboBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null) {
            return mf.valueToString(null);
        } else {
            return super.valueToString(value);
        }
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        try {
            return super.stringToValue(text);
        } catch (ParseException ex) {
            return null;
        }
    }
}