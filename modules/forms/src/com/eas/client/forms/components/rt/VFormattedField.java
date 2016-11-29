/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import com.eas.script.Scripts;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public abstract class VFormattedField extends JFormattedTextField implements HasValue<Object>, HasEmptyText, HasEditable {

    public static final int NUMBER = 0;
    public static final int DATE = 1;
    public static final int TIME = 2;
    public static final int PERCENT = 3;
    public static final int CURRENCY = 4;
    public static final int MASK = 5;
    public static final int REGEXP = 6;

    public class PolymorphFormatter extends AbstractFormatter {

        private String pattern;
        private final OptimisticMaskFormatter maskFormatter = new OptimisticMaskFormatter();
        private final SimpleDateFormat dateFormat = new SimpleDateFormat();
        private final DecimalFormat numberFormat = new DecimalFormat();
        private Pattern regexp;

        public PolymorphFormatter() {
            super();
            maskFormatter.setValueContainsLiteralCharacters(true);
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String aValue) {
            if (pattern == null ? aValue != null : !pattern.equals(aValue)) {
                pattern = aValue;
                try {
                    maskFormatter.setMask(pattern);
                } catch (ParseException ex) {
                    Logger.getLogger(VFormattedField.class.getName()).log(Level.WARNING, ex.getMessage());
                }
                try {
                    dateFormat.applyPattern(pattern);
                } catch (Exception ex) {
                    Logger.getLogger(VFormattedField.class.getName()).log(Level.WARNING, ex.getMessage());
                }
                try {
                    numberFormat.applyPattern(pattern);
                } catch (Exception ex) {
                    Logger.getLogger(VFormattedField.class.getName()).log(Level.WARNING, ex.getMessage());
                }
                try {
                    regexp = aValue != null && !aValue.isEmpty() ? Pattern.compile(aValue) : null;
                } catch (Exception ex) {
                    Logger.getLogger(VFormattedField.class.getName()).log(Level.WARNING, ex.getMessage());
                }
            }
        }

        @Override
        public Object stringToValue(String text) throws ParseException {
            if (onParse != null) {
                JSObject jsEvent = Scripts.getSpace().makeObj();
                jsEvent.setMember("source", getPublished());
                jsEvent.setMember("text", text);
                try {
                    return Scripts.getSpace().toJava(onParse.call(getPublished(), new Object[]{jsEvent}));
                } catch (Throwable t) {
                    throw new ParseException(text, 0);
                }
            } else {
                switch (valueType) {
                    case DATE:
                        return dateFormat.parse(text);
                    case TIME:
                        return dateFormat.parse(text);
                    case NUMBER:
                        return numberFormat.parse(text);
                    case PERCENT:
                        return numberFormat.parse(text);
                    case CURRENCY:
                        return numberFormat.parse(text);
                    case MASK:
                        return maskFormatter.stringToValue(text);
                    case REGEXP:
                        boolean matches = regexp != null ? regexp.matcher(text).matches() : true;
                        if (matches) {
                            return text;
                        } else {
                            throw new ParseException(text, 0);
                        }
                    default:
                        return text;
                }
            }
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (onFormat != null) {
                JSObject jsEvent = Scripts.getSpace().makeObj();
                jsEvent.setMember("source", getPublished());
                jsEvent.setMember("value", Scripts.getSpace().toJs(value));
                try {
                    return JSType.toString(onFormat.call(getPublished(), new Object[]{jsEvent}));
                } catch (Throwable t) {
                    throw new ParseException(t.getMessage(), 0);
                }
            } else {
                try {
                    switch (valueType) {
                        case DATE:
                            return dateFormat.format(value);
                        case TIME:
                            return dateFormat.format(value);
                        case NUMBER:
                            return numberFormat.format(value);
                        case PERCENT:
                            return numberFormat.format(value);
                        case CURRENCY:
                            return numberFormat.format(value);
                        case MASK:
                            return maskFormatter.valueToString(value);
                        case REGEXP:
                            return value != null ? value.toString() : "";
                        default:
                            return value != null ? value.toString() : "";
                    }
                } catch (IllegalArgumentException ex) {
                    throw new ParseException(ex.getMessage(), 0);
                }
            }
        }
    }

    protected PolymorphFormatter formatter = new PolymorphFormatter();
    protected int valueType = REGEXP;
    protected JSObject onFormat;
    protected JSObject onParse;
    protected boolean valueIsNull = true;
    protected PropertyChangeListener valueIsNullClearer = (PropertyChangeEvent pce) -> {
        valueIsNull = false;
    };

    public VFormattedField(Object aValue) {
        super();
        setFormatterFactory(new DefaultFormatterFactory(formatter));
        setValue(aValue);
    }

    public VFormattedField() {
        this(null);
    }

    protected abstract JSObject getPublished();

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int aValue) {
        if (valueType != aValue) {
            valueType = aValue;
        }
    }

    public String getFormat() {
        return formatter.getPattern();
    }

    public void setFormat(String aValue) {
        formatter.setPattern(aValue);
    }

    public JSObject getOnFormat() {
        return onFormat;
    }

    public void setOnFormat(JSObject aValue) {
        onFormat = aValue;
    }

    public JSObject getOnParse() {
        return onParse;
    }

    public void setOnParse(JSObject aValue) {
        onParse = aValue;
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
        return valueIsNull ? null : super.getValue();
    }

    @Override
    public void setValue(Object aValue) {
        if (aValue instanceof Number) {
            aValue = ((Number) aValue).doubleValue();
        }
        removeValueChangeListener(valueIsNullClearer);
        try {
            valueIsNull = aValue == null;
            super.setValue(aValue);
        } finally {
            addValueChangeListener(valueIsNullClearer);
        }
    }

    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Crazy Swing JFormattedField issues a value change while focus gaining
        if (!VALUE_PROP_NAME.equals(propertyName) || processedFocusEvent == null || processedFocusEvent.getID() == FocusEvent.FOCUS_LOST) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    private FocusEvent processedFocusEvent;

    @Override
    protected void processFocusEvent(FocusEvent e) {
        // Crazy Swing JFormattedField issues a value change while focus gaining
        processedFocusEvent = e;
        try {
            super.processFocusEvent(e);
        } finally {
            processedFocusEvent = null;
        }
    }

    @Override
    public void addValueChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(VALUE_PROP_NAME, listener);
    }

    @Override
    public void removeValueChangeListener(PropertyChangeListener listener) {
        super.removePropertyChangeListener(VALUE_PROP_NAME, listener);
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
