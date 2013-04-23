package com.eas.client.gxtcontrols.wrappers.component;

import com.sencha.gxt.cell.core.client.SliderCell;
import com.sencha.gxt.widget.core.client.Slider;

public class PlatypusSlider extends Slider {

	public PlatypusSlider() {
		super();
	}

	public PlatypusSlider(boolean vertical) {
		super(vertical);
	}

	public PlatypusSlider(SliderCell cell) {

		super(cell);
	}

	@Override
	public Integer getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Integer aValue) {
		super.setValue(aValue);
	}

	@Override
	public void setValue(Integer aValue, boolean fireEvents) {
		super.setValue(aValue, fireEvents);
	}

	// There is a bug in GXT. onBlur handlers call finishEditing, thus commiting
	// edited value.
	// It's OK, but when redraw() calls setInnerHTML(), browser fires onBlur
	// event against old
	// "input" element with old raw value.
	// Such old value is applied by GXT onBlur handler in standard way.
	// This leads to unexpected cancelling of new value.
	// It's luck, that this process acts as recursive call of setValue(,,).
	// So, we can protect setValue(,,) from recursion and solve the problem.
	protected boolean recurse;

	@Override
	public void setValue(Integer value, boolean fireEvents, boolean redraw) {
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
