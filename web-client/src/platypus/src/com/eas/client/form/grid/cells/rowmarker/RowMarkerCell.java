package com.eas.client.form.grid.cells.rowmarker;

import com.bearsoft.gwt.ui.widgets.grid.cells.RenderedEditorCell;
import com.eas.client.Utils.JsObject;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public abstract class RowMarkerCell extends AbstractCell<Object> {
	
	public RowMarkerCell() {
		super();
	}

	public abstract String getCursorProperty();
	
	public abstract JavaScriptObject getRowsData();
	
	@Override
	public void render(Context context, Object value, SafeHtmlBuilder sb) {
		RowMarkerResources.INSTANCE.style().ensureInjected();
		StringBuilder leftClasses = new StringBuilder();
		leftClasses.append(RowMarkerResources.INSTANCE.style().rowMarkerLeft());
		StringBuilder rightClasses = new StringBuilder();
		rightClasses.append(RowMarkerResources.INSTANCE.style().rowMarkerRight());
		JavaScriptObject rows = getRowsData();
		boolean currentRow = rows != null && rows.<JsObject>cast().getJs(getCursorProperty()) == value;
		if (currentRow)
			rightClasses.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerCurrent());
		/*
		if (value.isInserted())
			leftClasses.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerNew());
		else if (value.isUpdated())
			leftClasses.append(" ").append(RowMarkerResources.INSTANCE.style().rowMarkerEdited());
		*/		
		SafeHtmlBuilder content = new SafeHtmlBuilder();
		content.appendHtmlConstant("<div class=\"" + leftClasses.toString() + "\">&nbsp;</div><div class=\"" + rightClasses.toString() + "\">&nbsp;</div>");
		RenderedEditorCell.CellsResources.INSTANCE.tablecell().ensureInjected();
		sb.append(RenderedEditorCell.PaddedCell.INSTANCE.generate("", RenderedEditorCell.CellsResources.INSTANCE.tablecell().padded(), new SafeStylesBuilder().padding(RenderedEditorCell.CELL_PADDING, Style.Unit.PX).toSafeStyles(), content.toSafeHtml()));
	}
}
