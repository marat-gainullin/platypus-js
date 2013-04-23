package com.eas.client.gxtcontrols.grid;

import com.bearsoft.rowset.Row;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class GridCrossUpdaterAction implements Runnable {

	protected Grid<Row> grid;

	public GridCrossUpdaterAction(Grid<Row> aGrid) {
		super();
		grid = aGrid;
	}

	@Override
	public void run() {
		grid.getStore().fireEvent(new StoreUpdateEvent<Row>(grid.getStore().getAll()));
	}

}
