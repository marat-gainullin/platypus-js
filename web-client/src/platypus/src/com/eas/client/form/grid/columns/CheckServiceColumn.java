package com.eas.client.form.grid.columns;

import com.bearsoft.rowset.Row;
import com.eas.client.form.grid.cells.CheckBoxCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.SelectionModel;

public class CheckServiceColumn extends Column<Row, Boolean>{

	protected SelectionModel<Row> selectionModel;
	
	public CheckServiceColumn(SelectionModel<Row> aSelectionModel) {
	    super(new CheckBoxCell());
	    selectionModel = aSelectionModel;
    }

	@Override
    public Boolean getValue(Row object) {
	    return selectionModel.isSelected(object);
    }

}
