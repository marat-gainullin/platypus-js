package com.eas.client.form.grid;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.form.factories.GridFactory;
import com.eas.client.form.grid.selection.HasSelectionLead;
import com.eas.client.model.Entity;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

public class RowsetPositionSelectionHandler implements SelectionChangeEvent.Handler {

	protected SelectionModel<Row> selectionModel;
	protected Entity rowsSource;

	public RowsetPositionSelectionHandler(Entity aRowsSource, SelectionModel<Row> aSelectionModel) {
		super();
		selectionModel = aSelectionModel;
		rowsSource = aRowsSource;
	}

	@Override
    public void onSelectionChange(SelectionChangeEvent event) {
		if (rowsSource != null && selectionModel instanceof HasSelectionLead<?>) {
			try {				
				rowsSource.scrollTo(((HasSelectionLead<Row>)selectionModel).getLead());
			} catch (Exception e) {
				Logger.getLogger(GridFactory.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
    }

}
