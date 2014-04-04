package com.eas.client.form.published.widgets;

import com.bearsoft.gwt.ui.widgets.SpinnerBox;
import com.google.gwt.user.client.ui.ValueBox;

public class ConstraintedSpinnerBox extends SpinnerBox<Double> {
	
	protected double step = 1.0;
	protected Double min;
	protected Double max;

	public ConstraintedSpinnerBox(ValueBox<Double> aDecorated) {
		super(aDecorated);
	}

	@Override
	protected void increment() {
		Double oldValue = getValue();
		Double newValue = (oldValue != null ? oldValue : 0) + step;
		if (max == null || newValue <= max)
			setValue(newValue, true);
	}

	@Override
	protected void decrement() {
		Double oldValue = getValue();
		Double newValue = (oldValue != null ? oldValue : 0) - step;
		if (min == null || newValue >= min)
			setValue(newValue, true);
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double aValue) {
		min = aValue;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double aValue) {
		max = aValue;
	}

	public Double getStep() {
		return step;
	}

	public void setStep(Double aValue) {
		step = aValue;
	}
}
