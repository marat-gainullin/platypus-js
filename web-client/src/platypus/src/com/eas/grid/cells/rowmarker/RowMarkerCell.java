package com.eas.grid.cells.rowmarker;

import com.eas.core.Utils.JsObject;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public abstract class RowMarkerCell extends AbstractCell<Object> {
	
	public RowMarkerCell() {
		super();
	}

	public abstract String getCursorProperty();
	
	public abstract JavaScriptObject getRowsData();
	
	@Override
	public void render(Context context, Object value, SafeHtmlBuilder sb) {
		SafeHtmlBuilder content = new SafeHtmlBuilder();
		content.appendHtmlConstant("<div class=\"grid-cell-anchor\"></div>");
		JavaScriptObject rows = getRowsData();
		boolean currentRow = rows != null && rows.<JsObject>cast().getJs(getCursorProperty()) == value;
		if (currentRow)
			content.appendHtmlConstant("<div class=\"grid-marker-cell-cursor\"></div>");
		/*
		if (value.isInserted())
			content.appendHtmlConstant("<div class=\"grid-marker-inserted\"></div>");
		else if (value.isUpdated())
			content.appendHtmlConstant("<div class=\"grid-marker-cell-dirty\"></div>");
		*/		
		sb.append(content.toSafeHtml());
	}
}
