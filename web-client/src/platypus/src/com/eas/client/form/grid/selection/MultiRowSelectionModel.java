package com.eas.client.form.grid.selection;

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
	}

	@Override
	public void clear() {
		super.clear();
		lead = null;
	}

	@Override
	public Row getLead() {
		return lead;
	}
}
