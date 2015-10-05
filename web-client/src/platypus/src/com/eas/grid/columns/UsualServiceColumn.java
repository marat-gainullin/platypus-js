package com.eas.grid.columns;

import com.eas.grid.ModelGrid;
import com.eas.grid.cells.TreeExpandableCell;
import com.eas.grid.cells.rowmarker.RowMarkerCell;
import com.google.gwt.core.client.JavaScriptObject;

public class UsualServiceColumn extends ModelColumn {

	public UsualServiceColumn() {
		super();
		designedWidth = 22;
		minWidth = designedWidth;
		maxWidth = designedWidth;
		((TreeExpandableCell<JavaScriptObject, Object>) getCell()).setCell(new RowMarkerCell() {
			@Override
			public String getCursorProperty() {
				return ((ModelGrid) grid).getCursorProperty();
			}

			@Override
			public JavaScriptObject getRowsData() {
				return ((ModelGrid) grid).getData();
			}
		});
	}

	@Override
	public JavaScriptObject getValue(JavaScriptObject anElement) {
		return anElement;
	}
}
