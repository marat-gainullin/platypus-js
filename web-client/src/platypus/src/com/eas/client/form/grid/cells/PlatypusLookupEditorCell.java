/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form.grid.cells;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedEditorCell;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.eas.client.form.published.widgets.model.ModelCombo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class PlatypusLookupEditorCell extends RenderedEditorCell<Row> {

	public PlatypusLookupEditorCell() {
		super(new StyledListBox<Row>());
	}

	public PlatypusLookupEditorCell(Widget aEditor) {
		super(aEditor);
	}

	@Override
	protected void renderCell(Context context, Row valuesRow, SafeHtmlBuilder sb) {
		if (getEditor() instanceof ModelCombo) {
			try {
				ModelCombo combo = (ModelCombo) getEditor();
				if (combo.isValidBindings()) {
					Row displayRow = valuesRow;
					String label = displayRow != null ? combo.getConverter().convert(displayRow.getColumnObject(combo.getDisplayElement().getColIndex())) : "";
					sb.appendEscaped(label);
				}
			} catch (Exception e) {
				sb.appendEscaped(e.getMessage());
			}
		} else {
			sb.appendEscaped(String.valueOf(valuesRow));
		}
	}
}
