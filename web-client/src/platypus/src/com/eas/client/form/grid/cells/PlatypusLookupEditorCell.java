/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form.grid.cells;

import com.bearsoft.gwt.ui.widgets.StyledListBox;
import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedPopupEditorCell;
import com.bearsoft.rowset.Row;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class PlatypusLookupEditorCell extends RenderedPopupEditorCell<Row> {

	public PlatypusLookupEditorCell() {
		super(new StyledListBox<Row>());
	}

	public PlatypusLookupEditorCell(Widget aEditor) {
		super(aEditor);
	}

	@Override
	protected void renderCell(Context context, Row value, SafeHtmlBuilder sb) {
		sb.appendEscaped(String.valueOf(value));
	}
}
