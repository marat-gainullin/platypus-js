/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import de.wannawork.jcalendar.JCalendarComboBox;
import de.wannawork.jcalendar.JCalendarInvokerButton;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author mg
 */
public class VDateTimeField extends JCalendarComboBox implements HasValue<Date>, HasEditable, HasEmptyText {

    private static final Pattern TIME_PATTERN = Pattern.compile("[aHkKhmsSzZX]");
    private static final Pattern NO_TIME_PATTERN = Pattern.compile("[GyYMwWDdFEu]");
    public static final Date NULL_DATE_VALUE = new Date(-62135780400000L);

    protected Date oldValue;
    protected String dateFormat;
    protected String emptyText;

    public VDateTimeField() {
        super(true);
        oldValue = NULL_DATE_VALUE.equals(getModel().getValue()) ? null : (Date) getModel().getValue();
        getModel().addChangeListener((ChangeEvent e) -> {
            //Date newValue = NULL_DATE_VALUE.equals(getModel().getValue()) ? null : (Date) getModel().getValue();
            Date newValue = (Date)getModel().getValue();
            if (!Objects.equals(oldValue, newValue)) {
                Date oldValueWas = oldValue;
                oldValue = newValue;
                firePropertyChange(VALUE_PROP_NAME, oldValueWas, newValue);
            }
        });
    }

    private Date checkTime(Date aValue) {
        if (isOnlyDateFormat()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(aValue);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            return cal.getTime();
        } else {
            return aValue;
        }
    }

    private boolean isOnlyTimeFormat() {
        Matcher timeMatcher = TIME_PATTERN.matcher(dateFormat != null ? dateFormat : "");
        Matcher noTimeMatcher = NO_TIME_PATTERN.matcher(dateFormat != null ? dateFormat : "");
        return timeMatcher.find() && !noTimeMatcher.find();
    }

    private boolean isOnlyDateFormat() {
        Matcher timeMatcher = TIME_PATTERN.matcher(dateFormat != null ? dateFormat : "");
        Matcher noTimeMatcher = NO_TIME_PATTERN.matcher(dateFormat != null ? dateFormat : "");
        return !timeMatcher.find() && noTimeMatcher.find();
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String aValue) {
        if (dateFormat == null ? aValue != null : !dateFormat.equals(aValue)) {
            dateFormat = aValue != null ? aValue.trim() : null;
            super.setDateFormat(new SimpleDateFormat(dateFormat));
            JCalendarInvokerButton jb = getDropDownButton();
            if (jb != null) {
                jb.setVisible(!isOnlyTimeFormat());
            }
        }
    }

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
        JFormattedTextField tf = getEditorComponent();
        return tf.isEditable();
    }

    @Override
    public void setEditable(boolean aValue) {
        JFormattedTextField tf = getEditorComponent();
        tf.setEditable(aValue);
    }

    public String getText() {
        JFormattedTextField tf = getEditorComponent();
        return tf.getText();
    }

    public void setText(String aValue) throws Exception {
        JFormattedTextField tf = getEditorComponent();
        tf.setText(aValue != null ? aValue : "");
        tf.commitEdit();
    }

    @Override
    public void requestFocus() {
        getEditorComponent().requestFocus();
    }

    @Override
    public boolean requestFocus(boolean temporary) {
        return getEditorComponent().requestFocus(temporary);
    }

    @Override
    public Date getValue() {
        Date value = (Date) getModel().getValue();
        return value;
    }

    @Override
    public void setValue(Date aValue) {
        getModel().setValue(aValue);
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
