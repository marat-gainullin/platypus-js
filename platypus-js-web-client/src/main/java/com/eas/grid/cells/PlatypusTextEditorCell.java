package com.eas.grid.cells;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PlatypusTextEditorCell extends RenderedEditorCell<String> {

	public PlatypusTextEditorCell() {
		super(new TextBox());
	}
	
	public PlatypusTextEditorCell(Widget aEditor) {
		super(aEditor);
	}

	@Override
	protected void renderCell(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		sb.appendEscaped(value != null ? value : "");
	}

}
