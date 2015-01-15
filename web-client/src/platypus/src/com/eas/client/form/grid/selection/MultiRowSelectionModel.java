package com.eas.client.form.grid.selection;

import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.MultiSelectionModel;

public class MultiRowSelectionModel extends MultiSelectionModel<JavaScriptObject> implements HasSelectionLead<JavaScriptObject> {

	protected JavaScriptObject lead;
	protected ModelGrid grid;

	public MultiRowSelectionModel(ModelGrid aGrid) {
		super(new RowKeyProvider());
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
