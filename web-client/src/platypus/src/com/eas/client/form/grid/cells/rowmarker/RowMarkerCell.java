package com.eas.client.form.grid.cells.rowmarker;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class RowMarkerCell extends AbstractCell<Row> {
	
	protected Rowset rowsSource;

	public RowMarkerCell(Rowset aRowsSource) {
		super();
		rowsSource = aRowsSource;
	}

	public Rowset getRowsSource() {
		return rowsSource;
	}

	public void setRowsSource(Rowset aValue) {
		rowsSource = aValue;
	}

	@Override
	public void render(Context context, Row value, SafeHtmlBuilder sb) {
		RowMarkerResources.INSTANCE.style().ensureInjected();
		StringBuilder bl = new StringBuilder();
		bl.append(RowMarkerResources.INSTANCE.style().rowMarkerLeft());
		StringBuilder br = new StringBuilder();
		br.append(RowMarkerResources.INSTANCE.style().rowMarkerRight());
		boolean currentRow = rowsSource != null && rowsSource.getCurrentRow() == value;
		if (currentRow)
			br.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
		if (value.isInserted())
			bl.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerNew());
		else if (value.isUpdated())
			bl.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerEdited());
		sb.appendHtmlConstant("<div class=\"" + bl.toString() + "\">&nbsp;</div><div class=\"" + br.toString() + "\">&nbsp;</div>");
	}
}
