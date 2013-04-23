package com.eas.client.gxtcontrols.wrappers.component;

import java.text.ParseException;

import com.eas.client.Utils;

public class PlatypusFormattedTextField extends FormattedTextField {

	/**
	 * Grid-inline editing constructor
	 */
	public PlatypusFormattedTextField(ObjectFormat aFormat) {
		super(new FormattedTextPropertyEditor(aFormat));
	}

	public PlatypusFormattedTextField(FormattedTextInputCell aCell) {
		super(aCell, aCell.getPropertyEditor());
	}

	public String getFormat() {
		ObjectFormat of = getPropertyEditor().getFormat();
		return of != null ? of.getPattern() : null;
	}

	public void setFormat(String aPattern) throws ParseException {
		ObjectFormat of = getPropertyEditor().getFormat();
		if (of != null) {
			of.setPattern(aPattern);
		}
	}

	@Override
	public Object getValue() {
		return super.getValue();
	}

	public Object getJsValue() {
		return Utils.toJs(getValue());
	}

	@Override
	public void setValue(Object value, boolean fireEvents) {
		super.setValue(value, fireEvents);
	}

	public void setJsValue(Object aValue) throws Exception {
		setValue(Utils.toJava(aValue), true);
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
	public void setValue(Object value, boolean fireEvents, boolean redraw) {
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
