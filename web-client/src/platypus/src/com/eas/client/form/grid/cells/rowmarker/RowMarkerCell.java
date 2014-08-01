package com.eas.client.form.grid.cells.rowmarker;

import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedEditorCell;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
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
		StringBuilder leftClasses = new StringBuilder();
		leftClasses.append(RowMarkerResources.INSTANCE.style().rowMarkerLeft());
		StringBuilder rightClasses = new StringBuilder();
		rightClasses.append(RowMarkerResources.INSTANCE.style().rowMarkerRight());
		boolean currentRow = rowsSource != null && rowsSource.getCurrentRow() == value;
		if (currentRow)
			rightClasses.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
		if (value.isInserted())
			leftClasses.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerNew());
		else if (value.isUpdated())
			leftClasses.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerEdited());
		
		SafeHtmlBuilder content = new SafeHtmlBuilder();
		content.appendHtmlConstant("<div class=\"" + leftClasses.toString() + "\">&nbsp;</div><div class=\"" + rightClasses.toString() + "\">&nbsp;</div>");
		RenderedEditorCell.CellsResources.INSTANCE.tablecell().ensureInjected();
		sb.append(RenderedEditorCell.PaddedCell.INSTANCE.generate("", RenderedEditorCell.CellsResources.INSTANCE.tablecell().padded(), new SafeStylesBuilder().padding(RenderedEditorCell.CELL_PADDING, Style.Unit.PX).toSafeStyles(), content.toSafeHtml()));
	}
}
