package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.eas.client.form.grid.cells.rowmarker.RowMarkerCell;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.google.gwt.core.client.JavaScriptObject;

public class UsualServiceColumn extends ModelColumn {

	public UsualServiceColumn() {
		super();
		designedWidth = 22;
		minWidth = designedWidth;
		maxWidth = designedWidth;
		((TreeExpandableCell<JavaScriptObject, Object>)getCell()).setCell(new RowMarkerCell(){

			@Override
            public JavaScriptObject getRowsData() {
	            return ((ModelGrid)grid).getData();
            }});
	}

	@Override
	public JavaScriptObject getValue(JavaScriptObject anElement) {
		return anElement;
	}
}
