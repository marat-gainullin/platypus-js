package com.eas.grid.cells;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public interface CellRenderer<V> {

	public boolean render(com.google.gwt.cell.client.Cell.Context context, String aId, V value, SafeHtmlBuilder sb);
}
