/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wannawork.jcalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.DateFormatter;
import javax.swing.text.MaskFormatter;

/**
 * This class makes date formatting while string is incomplete, like 12.12.____ for dd/mm/yyyy mask.
 * Standard formatters can't format strings like this, but we need to display them.
 * @author mg
 */
public class OptimisticDateFormatter extends DateFormatter {

    public static final String COMPLEMENTABLE_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_TAIL = "  :  :  ";

    public static class StringContainer extends Date {

        public String view;

        public StringContainer(String aValue) {
            super();
            view = aValue;
        }
    }
    protected MaskFormatter mf = new MaskFormatter();
    protected Pattern patt = Pattern.compile("[\\d\\.\\: ]+");

    public OptimisticDateFormatter(SimpleDateFormat aFormat) {
        super(aFormat);
        String datePattern = aFormat.toPattern();
        String regexPattern = datePattern.replaceAll("[mMhHsYyda]", "[\\\\d ]");
        regexPattern = regexPattern.replaceAll("\\.", "\\\\.");
        regexPattern = regexPattern.replaceAll("\\:", "\\\\:");
        patt = Pattern.compile(regexPattern);
        try {
            mf.setMask(datePattern.replaceAll("[mMhHsYydaS]", "#"));
        } catch (ParseException ex) {
            Logger.getLogger(OptimisticDateFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null) {
            return mf.valueToString(null);
        } else if (value instanceof StringContainer) {
            return ((StringContainer) value).view;
        } else {
            return super.valueToString(value);
        }
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        if (text == null
                || text.isEmpty()
                || mf.valueToString(null).equals(text)) {
            return null;
        } else {
            try {
                Object value = super.stringToValue(text);
                if (super.valueToString(value).trim().length() != text.trim().length()) {
                    throw new ParseException(text, text.length() - 1);
                }
                return value;
            } catch (ParseException ex) {
                Matcher mat = patt.matcher(text);
                if (mat.matches()) {
                    if (COMPLEMENTABLE_FORMAT_PATTERN.equals(((SimpleDateFormat) getFormat()).toPattern())
                            && text.endsWith(DATETIME_FORMAT_TAIL)) {
                        try {
                            String complementedText = text.substring(0, text.length() - DATETIME_FORMAT_TAIL.length()) + "00:00:00";
                            Object value = super.stringToValue(complementedText);
                            if (!super.valueToString(value).equals(complementedText)) {
                                throw new ParseException(complementedText, complementedText.length() - 1);
                            }
                            return value;
                        } catch (ParseException ex1) {
                            return new StringContainer(text);
                        }
                    } else {
                        return new StringContainer(text);
                    }
                } else {
                    throw ex;
                }
            }
        }
    }
}
