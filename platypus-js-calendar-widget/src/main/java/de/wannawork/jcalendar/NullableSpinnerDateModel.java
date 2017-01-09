/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.wannawork.jcalendar;

import javax.swing.SpinnerDateModel;

/**
 *
 * @author mg
 */
public class NullableSpinnerDateModel extends SpinnerDateModel {

    protected boolean isNull;

    public NullableSpinnerDateModel() {
        super();
        setValue(null);
    }

    @Override
    public Object getValue() {
        if (isNull) {
            return null;
        } else {
            return super.getValue();
        }
    }

    @Override
    public void setValue(Object value) {
        if (!(value instanceof OptimisticDateFormatter.StringContainer)) {
            boolean oldIsNull = isNull;
            isNull = value == null;
            if (!isNull) {
                super.setValue(value);
            }
            if (isNull != oldIsNull) {
                fireStateChanged();
            }
        }
    }
}
