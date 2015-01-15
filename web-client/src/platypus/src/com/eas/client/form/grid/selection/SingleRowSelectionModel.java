package com.eas.client.form.grid.selection;

import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.SingleSelectionModel;

public class SingleRowSelectionModel extends SingleSelectionModel<JavaScriptObject> implements HasSelectionLead<JavaScriptObject> {

	protected JavaScriptObject lead;
	protected ModelGrid grid;

	public SingleRowSelectionModel(ModelGrid aGrid) {
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
		updateRow(item);
	}

	protected void updateRow(JavaScriptObject item) {
		if (item != null && grid.getDataProvider() instanceof IndexOfProvider<?>) {
			int rowIndex = ((IndexOfProvider<JavaScriptObject>) grid.getDataProvider()).indexOf(item);
			if (rowIndex != -1) {
				grid.getDataProvider().getList().set(rowIndex, item);
			}
		}
	}

	@Override
	public JavaScriptObject getLead() {
		return lead;
	}
}
