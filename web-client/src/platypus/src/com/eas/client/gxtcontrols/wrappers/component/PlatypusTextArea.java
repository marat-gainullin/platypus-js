package com.eas.client.gxtcontrols.wrappers.component;

import com.sencha.gxt.cell.core.client.form.TextAreaInputCell;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class PlatypusTextArea extends TextArea {

	public PlatypusTextArea() {
		super();
	}

	public PlatypusTextArea(TextAreaInputCell aCell) {
		super(aCell);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		getFocusEl().setAttribute("wrap", "off");
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		super.setValue(value, fireEvents);
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
				if("".equals(value))
					value = null;
				super.setValue(value, fireEvents, redraw);
			} finally {
				recurse = false;
			}
		}
	}
}
