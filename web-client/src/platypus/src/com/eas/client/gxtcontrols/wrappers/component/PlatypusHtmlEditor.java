package com.eas.client.gxtcontrols.wrappers.component;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sencha.gxt.widget.core.client.form.HtmlEditor;

public class PlatypusHtmlEditor extends HtmlEditor {

	public PlatypusHtmlEditor() {
		super();
	}

	@Override
	protected void onEnable() {
		try {// to fix GXT's bug
			super.onEnable();
		} catch (Exception ex) {
			Logger.getLogger(PlatypusHtmlEditor.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	protected void onDisable() {
		try {// to fix GXT's bug
			super.onDisable();
		} catch (Exception ex) {
			Logger.getLogger(PlatypusHtmlEditor.class.getName()).log(Level.SEVERE, ex.getMessage());
		}
	}

	@Override
	public String getValue() {
		return super.getValue();
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
	public void setValue(String value) {
		if (!recurse) {
			recurse = true;
			try {
				super.setValue(value);
			} finally {
				recurse = false;
			}
		}
	}

}
