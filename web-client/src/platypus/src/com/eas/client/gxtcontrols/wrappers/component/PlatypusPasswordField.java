package com.eas.client.gxtcontrols.wrappers.component;

import com.eas.client.gxtcontrols.ControlsUtils;
import com.sencha.gxt.cell.core.client.form.PasswordInputCell;
import com.sencha.gxt.widget.core.client.form.PasswordField;

public class PlatypusPasswordField extends PasswordField {

	public PlatypusPasswordField() {
		super();
	}

	public PlatypusPasswordField(PasswordInputCell aCell) {
		super(aCell);
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
	public void setValue(String value, boolean fireEvents, boolean redraw) {
		if (!recurse) {
			recurse = true;
			try {
				super.setValue(value, fireEvents, redraw);
			} finally {
				recurse = false;
			}
		}
	}
	
	@Override
	public String getEmptyText() {
		return ControlsUtils.getEmptyText(this);
	}
	
	@Override
	public void setEmptyText(String emptyText) {
		ControlsUtils.setEmptyText(this, emptyText);
	}
}
