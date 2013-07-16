package com.eas.client.gxtcontrols.wrappers.component;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class PlatypusComboBox extends ComboBox<Object> {

	public PlatypusComboBox(ComboBoxCell<Object> aCell) {
		super(aCell);
		addSelectionHandler(new SelectionHandler<Object>(){

			@Override
            public void onSelection(SelectionEvent<Object> event) {
				finishEditing();
            }
			
		});
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
