/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import com.eas.bound.ModelCombo;
import com.eas.widgets.boxes.StyledListBox;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class PlatypusLookupEditorCell extends RenderedEditorCell<Object> {

	public PlatypusLookupEditorCell() {
		super(new StyledListBox<Object>());
	}

	public PlatypusLookupEditorCell(Widget aEditor) {
		super(aEditor);
	}

	@Override
	protected void renderCell(Context context, Object value, SafeHtmlBuilder sb) {
		/*
		if (getEditor() instanceof ModelCombo) {
			try {
				ModelCombo combo = (ModelCombo) getEditor();
				if (combo.isValidBindings()) {
					Row displayRow = combo.getLookup().lookupRow(value);
					String label = displayRow != null ? combo.getConverter().convert(displayRow.getColumnObject(combo.getDisplayElement().getColIndex())) : "";
					sb.appendEscaped(label);
				}
			} catch (Exception e) {
				sb.appendEscaped(e.getMessage());
			}
		} else {
			sb.appendEscaped(String.valueOf(value));
		}
				*/
	}
}
