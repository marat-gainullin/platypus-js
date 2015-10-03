package com.eas.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.eas.grid.cells.CheckBoxCell;
import com.google.gwt.core.client.JavaScriptObject;

public class CheckServiceColumn extends ModelColumn {

	public CheckServiceColumn() {
	    super(new TreeExpandableCell<JavaScriptObject, Object>(new CheckBoxCell()));
		designedWidth = 22;
		minWidth = designedWidth;
		maxWidth = designedWidth;
    }

	@Override
    public Boolean getValue(JavaScriptObject object) {
	    return grid.getSelectionModel().isSelected(object);
    }

}
