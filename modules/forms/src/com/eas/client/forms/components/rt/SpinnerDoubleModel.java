/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author mg
 */
public class SpinnerDoubleModel extends SpinnerNumberModel {

    protected Double value;
    protected Double min;
    protected Double max;
    protected double step;
    protected Set<PropertyChangeListener> valueChangeListeners = new HashSet<>();

    public SpinnerDoubleModel(Double aValue, Double aMin, Double aMax, double aStep) {
        super();
        value = aValue;
        step = aStep;
        setMin(aMin);
        setMax(aMax);
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double aMin) {
        if (!Objects.equals(min, aMin)) {
            min = aMin;
        }
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double aMax) {
        if (!Objects.equals(max, aMax)) {
            max = aMax;
        }
    }

    public double getStep() {
        return step;
    }

    public void setStep(double aValue) {
        step = aValue;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object oValue) {
        if (oValue instanceof Double && ((Double) oValue).isNaN()) {
            oValue = null;
        }
        if (oValue != null) {
            assert oValue instanceof Double;
            Double aValue = (Double) oValue;
            if (!Objects.equals(value, aValue)) {
                Double newValue = checkValue(aValue);
                if (!Objects.equals(value, newValue)) {
                    Double oldValue = value;
                    value = newValue;
                    fireValueChanged(oldValue, newValue);
                }
            }
        } else if (value != null) {
            Double oldValue = value;
            value = null;
            fireValueChanged(oldValue, null);
        }
    }

    @Override
    public Object getNextValue() {
        if (value == null) {
            value = 0.0d;
        }
        return incrementedValue(true);
    }

    @Override
    public Object getPreviousValue() {
        if (value == null) {
            value = 0.0d;
        }
        return incrementedValue(false);
    }

    private Double incrementedValue(boolean dir) {
        double newValue = value + step * (dir ? 1 : -1);
        if ((max != null) && (max.compareTo(newValue) < 0)) {
            return null;
        }
        if ((min != null) && (min.compareTo(newValue) > 0)) {
            return null;
        } else {
            return newValue;
        }
    }

    private Double checkValue(Double aValue) {
        if (max != null && max.compareTo(aValue) < 0) {
            return max;
        }
        if (min != null && min.compareTo(aValue) > 0) {
            return min;
        }
        return aValue;
    }

    public void addValueChangeListener(PropertyChangeListener l) {
        valueChangeListeners.add(l);
    }

    public void removeValueChangeListener(PropertyChangeListener l) {
        valueChangeListeners.add(l);
    }

    public PropertyChangeListener[] getValueChangeListeners() {
        return valueChangeListeners.toArray(new PropertyChangeListener[]{});
    }

    protected void fireValueChanged(Double aOldValue, Double aNewValue) {
        super.fireStateChanged();
        PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, "value", aOldValue, aNewValue);
        valueChangeListeners.stream().forEach((l) -> {
            l.propertyChange(changeEvent);
        });
    }
}
