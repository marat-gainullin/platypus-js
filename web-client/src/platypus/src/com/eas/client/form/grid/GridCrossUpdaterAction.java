package com.eas.client.form.grid;

import com.eas.client.form.published.widgets.model.ModelGrid;
import com.google.gwt.core.client.Callback;
import com.bearsoft.rowset.events.RowsetEvent;


public class GridCrossUpdaterAction implements Callback<RowsetEvent, RowsetEvent> {

	protected ModelGrid grid;

	public GridCrossUpdaterAction(ModelGrid aGrid) {
		super();
		grid = aGrid;
	}

	@Override
    public void onFailure(RowsetEvent reason) {
    }

	@Override
    public void onSuccess(RowsetEvent result) {
    }

}
