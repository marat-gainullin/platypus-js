package com.eas.client.form.grid.selection;

import com.bearsoft.rowset.Row;
import com.eas.client.form.RowKeyProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class SingleRowSelectionModel extends SingleSelectionModel<Row> implements HasSelectionLead<Row>{

	protected Row lead;

	public SingleRowSelectionModel() {
		super(new RowKeyProvider());
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
	public Row getLead() {
		return lead;
	}
}
