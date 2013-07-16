package com.eas.client.gxtcontrols.wrappers.component;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.sencha.gxt.cell.core.client.form.ToggleButtonCell;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.ToggleButton;

public class PlatypusToggleButton extends ToggleButton implements HasGroup {

	protected PlatypusButtonGroup group;

	public PlatypusToggleButton() {
		super();
	}

	public PlatypusToggleButton(String aText) {
		super(aText);
	}

	public PlatypusToggleButton(ToggleButtonCell aCell) {
		super(aCell);
	}

	@Override
	public PlatypusButtonGroup getButtonGroup() {
		return group;
	}

	@Override
	public void setButtonGroup(PlatypusButtonGroup aGroup) {
		group = aGroup;
	}

	@Override
	public void mutateButtonGroup(PlatypusButtonGroup aGroup) {
		if (group != aGroup) {
			if (group != null)
				group.remove((Component) this);
			group = aGroup;
			if (group != null)
				group.add((Component) this);
		}
	}

	@Override
	public Boolean getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(Boolean aValue) {
		super.setValue(aValue);
	}

	@Override
	public void setValue(Boolean aValue, boolean fireEvents) {
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
	public void setValue(Boolean value, boolean fireEvents, boolean redraw) {
		if (!recurse) {
			recurse = true;
			try {
				super.setValue(value, fireEvents, redraw);
			} finally {
				recurse = false;
			}
		}
	}

	public boolean getPlainValue() {
		Boolean v = getValue();
		return v != null ? v : false;
	}

	public void setPlainValue(boolean aValue) {
		setValue(aValue);
	}

	@Override
	protected void onRedraw() {
		super.onRedraw();
		ControlsUtils.reapplyStyle(this);
	}
}
