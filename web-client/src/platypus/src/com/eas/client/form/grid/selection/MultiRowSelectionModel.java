package com.eas.client.form.grid.selection;

import com.bearsoft.gwt.ui.widgets.grid.processing.IndexOfProvider;
import com.bearsoft.rowset.Row;
import com.eas.client.form.RowKeyProvider;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.google.gwt.view.client.MultiSelectionModel;

public class MultiRowSelectionModel extends MultiSelectionModel<Row> implements HasSelectionLead<Row> {

	protected Row lead;
	protected ModelGrid grid;

	public MultiRowSelectionModel(ModelGrid aGrid) {
		super(new RowKeyProvider());
		grid = aGrid;
	}

	@Override
	public void setSelected(Row item, boolean selected) {
		if (selected)
			lead = item;
		else if (item == lead)
			lead = null;
		super.setSelected(item, selected);
		updateRow(item);
	}

	@Override
	public void clear() {
		super.clear();
		updateRow(lead);
		lead = null;
	}

	protected void updateRow(Row item) {
		if (item != null && grid.getDataProvider() instanceof IndexOfProvider<?>) {
			int rowIndex = ((IndexOfProvider<Row>) grid.getDataProvider()).indexOf(item);
			if (rowIndex != -1) {
				grid.getDataProvider().getList().set(rowIndex, item);
			}
		}
	}

	@Override
	public Row getLead() {
		return lead;
	}
}
