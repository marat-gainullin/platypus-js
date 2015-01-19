package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.google.gwt.core.client.JavaScriptObject;

public class CheckServiceColumn extends ModelColumn {

	public CheckServiceColumn() {
	    super();
		designedWidth = Grid.MINIMUM_COLUMN_WIDTH;
		minWidth = designedWidth;
		maxWidth = designedWidth;
    }

	@Override
    public Boolean getValue(JavaScriptObject object) {
	    return grid.getSelectionModel().isSelected(object);
    }

}
