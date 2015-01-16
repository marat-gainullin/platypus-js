package com.eas.client.form.grid.columns;

import com.google.gwt.core.client.JavaScriptObject;

public class CheckServiceColumn extends ModelColumn {

	public CheckServiceColumn() {
	    super();
    }

	@Override
    public Boolean getValue(JavaScriptObject object) {
	    return grid.getSelectionModel().isSelected(object);
    }

}
