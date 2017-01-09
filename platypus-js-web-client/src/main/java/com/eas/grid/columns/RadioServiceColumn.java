package com.eas.grid.columns;

import com.eas.grid.ModelGrid;
import com.eas.grid.cells.CheckBoxCell;
import com.eas.grid.cells.TreeExpandableCell;
import com.google.gwt.core.client.JavaScriptObject;

public class RadioServiceColumn extends ModelColumn {

	public RadioServiceColumn() {
		super(new TreeExpandableCell<JavaScriptObject, Object>(new CheckBoxCell("")));
		designedWidth = 22;
		minWidth = designedWidth;
		maxWidth = designedWidth;
	}

	@Override
	public Boolean getValue(JavaScriptObject object) {
		return grid.getSelectionModel().isSelected(object);
	}

	@Override
	public void setGrid(ModelGrid aValue) {
		((CheckBoxCell) getTargetCell()).setGroupName("");
		super.setGrid(aValue);
		if (aValue != null)
			((CheckBoxCell) getTargetCell()).setGroupName(aValue.getGroupName());
	}
}
