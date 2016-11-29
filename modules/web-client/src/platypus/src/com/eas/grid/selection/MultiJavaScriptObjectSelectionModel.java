package com.eas.grid.selection;

import com.eas.grid.ModelGrid;
import com.eas.ui.JavaScriptObjectKeyProvider;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.MultiSelectionModel;

public class MultiJavaScriptObjectSelectionModel extends MultiSelectionModel<JavaScriptObject> implements HasSelectionLead<JavaScriptObject> {

	protected JavaScriptObject lead;
	protected ModelGrid grid;

	public MultiJavaScriptObjectSelectionModel(ModelGrid aGrid) {
		super(new JavaScriptObjectKeyProvider());
		grid = aGrid;
	}

	@Override
	public void setSelected(JavaScriptObject item, boolean selected) {
		if (selected)
			lead = item;
		else if (item == lead)
			lead = null;
		super.setSelected(item, selected);
	}

	@Override
	public void clear() {
		super.clear();
		lead = null;
	}

	@Override
	public JavaScriptObject getLead() {
		return lead;
	}
}
