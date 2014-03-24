package com.eas.client.form.grid;

import com.eas.client.form.published.widgets.model.ModelGrid;


public class GridCrossUpdaterAction implements Runnable {

	protected ModelGrid grid;

	public GridCrossUpdaterAction(ModelGrid aGrid) {
		super();
		grid = aGrid;
	}

	@Override
	public void run() {
		assert false : "GridCrossUpdaterAction is not implemented yet.";
	}

}
