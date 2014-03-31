package com.eas.client.form.grid.columns;

import com.bearsoft.rowset.Row;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.SelectionModel;

public class CheckServiceColumn extends Column<Row, Boolean>{

	protected SelectionModel<Row> selectionModel;
	
	public CheckServiceColumn(SelectionModel<Row> aSelectionModel) {
	    super(new CheckboxCell(false, true));
	    selectionModel = aSelectionModel;
    }

	@Override
    public Boolean getValue(Row object) {
	    return selectionModel.isSelected(object);
    }

}
