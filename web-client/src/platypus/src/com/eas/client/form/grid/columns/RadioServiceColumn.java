package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.eas.client.form.grid.cells.CheckBoxCell;
import com.eas.client.form.published.widgets.model.ModelGrid;
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
