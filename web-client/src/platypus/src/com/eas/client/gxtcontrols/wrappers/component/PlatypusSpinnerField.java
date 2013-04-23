package com.eas.client.gxtcontrols.wrappers.component;

import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell;
import com.sencha.gxt.widget.core.client.form.SpinnerField;

public class PlatypusSpinnerField extends SpinnerField<Double> {

	public PlatypusSpinnerField(SpinnerFieldCell<Double> aCell) {
		super(aCell);
		setAllowNegative(true);
	}

	public Double getValue() {
		return super.getValue();
	}

	public void setValue(Double aValue, boolean fireEvents) {
		setValue(aValue, fireEvents, true);
	}

	// There is a bug in GXT. onBlur handlers call finishEditing, thus commiting edited value.
	// It's OK, but when redraw() calls setInnerHTML(), browser fires onBlur event against old 
	// "input" element with old raw value.
	// Such old value is applied by GXT onBlur handler in standard way.
	// This leads to unexpected cancelling of new value.
	// It's luck, that this process acts as recursive call of setValue(,,).
	// So, we can protect setValue(,,) from recursion and solve the problem.
	protected boolean recurse;

	@Override
	public void setValue(Double value, boolean fireEvents, boolean redraw) {
		if (!recurse) {
			recurse = true;
			try {
				super.setValue(value, fireEvents, redraw);
			} finally {
				recurse = false;
			}
		}
	}
}
