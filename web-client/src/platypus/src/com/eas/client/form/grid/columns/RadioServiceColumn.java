package com.eas.client.form.grid.columns;

import com.bearsoft.rowset.Row;
import com.eas.client.form.grid.cells.RadioButtonCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.SelectionModel;

public class RadioServiceColumn extends Column<Row, Boolean>{

	protected SelectionModel<Row> selectionModel;
	
	public RadioServiceColumn(SelectionModel<Row> aSelectionModel) {
	    super(new RadioButtonCell(false, true));
	    selectionModel = aSelectionModel;
    }

	@Override
    public Boolean getValue(Row object) {
	    return selectionModel.isSelected(object);
    }

}
