/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.spin.rt;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author mg
 */
public class NullableSpinnerNumberModel extends SpinnerNumberModel {

    protected Number value;
    protected BigDecimal min = null;
    protected BigDecimal max = null;
    protected Number step;
    protected boolean editable = true;
    protected Set<ChangeListener> valueChangeListeners = new HashSet<>();

    public NullableSpinnerNumberModel(Number aValue, Number aMin, Number aMax, Number aStep) {
        super();
        value = aValue;
        step = aStep;
        setMin(aMin);
        setMax(aMax);
    }

    public void setMin(Number aMin) {
        if (aMin != null) {
            min = new BigDecimal(((Number) aMin).doubleValue());
        }
    }

    public void setMax(Number aMax) {
        if (aMax != null) {
            max = new BigDecimal(((Number) aMax).doubleValue());
        }
    }

    public void setStep(Number aValue) {
        if (aValue != null) {
            step = aValue;
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object aValue) {
        Object oldValue = value;
        if (aValue == null) {
            value = null;
            if (oldValue != null) {
                fireStateChanged();
            }
        } else if (aValue instanceof Number) {
            value = checkValue((Number) aValue);
            if (oldValue == null || !oldValue.equals(aValue)) {
                fireStateChanged();
            }
        } else if (aValue != null && value == null) {
            fireStateChanged();
        }
    }

    @Override
    public Object getNextValue() {
        if (editable) {
            if (value == null) {
                return min != null ? min : 0;
            } else {
                return incrementValue(value, 1);
            }
        } else {
            return value;
        }
    }

    @Override
    public Object getPreviousValue() {
        if (editable) {
            if (value == null) {
                return max != null ? max : 0;
            } else {
                return incrementValue(value, -1);
            }
        } else {
            return value;
        }
    }

    private Number incrementValue(Number aValue, int dir) {
        Number newValue;
        BigDecimal lnewValue;
        if ((step instanceof Float) || (step instanceof Double)) {
            double v = aValue.doubleValue() + (step.doubleValue() * (double) dir);
            if (aValue instanceof BigDecimal) {
                newValue = new BigDecimal(v);
            } else if (aValue instanceof Double) {
                newValue = new Double(v);
            } else {
                newValue = new Float(v);
            }
            lnewValue = new BigDecimal(v);
        } else {
            long v = aValue.longValue() + (step.longValue() * (long) dir);

            if (aValue instanceof Long) {
                newValue = new Long(v);
            } else if (aValue instanceof Integer) {
                newValue = new Integer((int) v);
            } else if (aValue instanceof Short) {
                newValue = new Short((short) v);
            } else {
                newValue = new Byte((byte) v);
            }
            lnewValue = new BigDecimal(v);
        }

        if ((max != null) && (max.compareTo(lnewValue) < 0)) {
            return null;
        }
        if ((min != null) && (min.compareTo(lnewValue) > 0)) {
            return null;
        } else {
            return newValue;
        }
    }

    private Number checkValue(Number aValue) {
        BigDecimal lnewValue;
        if ((aValue instanceof Float) || (aValue instanceof Double) || (aValue instanceof BigDecimal)) {
            if (aValue instanceof BigDecimal && ((BigDecimal) aValue).precision() - ((BigDecimal) aValue).scale() > 0) {
                lnewValue = new BigDecimal(((BigDecimal) aValue).unscaledValue().longValue());
            } else {
                lnewValue = new BigDecimal(aValue.doubleValue());
            }
        } else {
            lnewValue = new BigDecimal(aValue.longValue());
        }

        if ((max != null) && (max.compareTo(lnewValue) < 0)) {
            return max;
        }
        if ((min != null) && (min.compareTo(lnewValue) > 0)) {
            return min;
        } else {
            return aValue;
        }
    }

    public void addValueChangeListener(ChangeListener l) {
        valueChangeListeners.add(l);
    }

    public void removeValueChangeListener(ChangeListener l) {
        valueChangeListeners.add(l);
    }

    public ChangeListener[] getValueChangeListeners() {
        return valueChangeListeners.toArray(new ChangeListener[]{});
    }

    @Override
    protected void fireStateChanged() {
        super.fireStateChanged();
        ChangeEvent changeEvent = new ChangeEvent(this);
        for (ChangeListener l : valueChangeListeners) {
            l.stateChanged(changeEvent);
        }
    }
}
