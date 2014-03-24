package com.eas.client.form.grid.rowmarker;

import com.bearsoft.rowset.Row;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class RowMarkerCell extends AbstractCell<Row> {
	protected Entity rowsSource;

	public RowMarkerCell(Entity aRowsSource) {
		super();
		rowsSource = aRowsSource;
	}

	public Entity getRowsSource() {
		return rowsSource;
	}

	public void setRowsSource(Entity aValue) {
		rowsSource = aValue;
	}

	@Override
	public void render(Context context, Row value, SafeHtmlBuilder sb) {
		RowMarkerResources.INSTANCE.style().ensureInjected();
		StringBuilder bl = new StringBuilder();
		bl.append(RowMarkerResources.INSTANCE.style().rowMarkerLeft());
		StringBuilder br = new StringBuilder();
		br.append(RowMarkerResources.INSTANCE.style().rowMarkerRight());
		boolean currentRow = rowsSource.getRowset() != null && rowsSource.getRowset().getCurrentRow() == value;
		if (currentRow)
			br.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
		if (value.isInserted())
			bl.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerNew());
		else if (value.isUpdated())
			bl.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerEdited());
		sb.appendHtmlConstant("<div class=\"" + bl.toString() + "\">&nbsp;</div><div class=\"" + br.toString() + "\">&nbsp;</div>");
	}
}
