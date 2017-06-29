package com.eas.widgets;

import com.eas.ui.HasNumberValue;
import com.google.gwt.dom.client.Document;

/**
 *
 * @author mgainullin
 */
public class NumberField extends TextField implements HasNumberValue {

    protected double step = 1.0;
    protected Double min;
    protected Double max;
    protected DecimalFormat format = new DecimalFormat();

    public NumberField() {
        super(Document.get().createNumberInputElement());
    }

    @Override
    protected String format(Object aValue) {
        return format.format(aValue);
    }

    @Override
    protected Object parse(String aText) {
        return format.parse(aText);
    }

    public void increment() {
        Double oldValue = (Double) getValue();
        Double newValue = (oldValue != null ? oldValue : 0) + step;
        if (max == null || newValue <= max) {
            setValue(newValue);
        }
    }

    public void decrement() {
        Double oldValue = (Double) getValue();
        Double newValue = (oldValue != null ? oldValue : 0) - step;
        if (min == null || newValue >= min) {
            setValue(newValue);
        }
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double aValue) {
        min = aValue;
        element.setAttribute("min", aValue + "");
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double aValue) {
        max = aValue;
        element.setAttribute("max", aValue + "");
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double aValue) {
        step = aValue;
        element.setAttribute("step", aValue + "");
    }
}
